package com.icuxika.service;

import com.icuxika.exception.GlobalServiceException;
import com.icuxika.vo.VerificationImageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class VerificationServiceImpl implements VerificationService {

    private static final Logger L = LoggerFactory.getLogger(VerificationServiceImpl.class);
    private static final int DEFAULT_IMAGE_WIDTH = 280;
    private static final int DEFAULT_IMAGE_HEIGHT = 171;

    private final Map<String, VerificationImageInfo> cacheMap = new HashMap<>();

    @Override
    public VerificationImageInfo refresh() {
        VerificationImageInfo info = new VerificationImageInfo();
        try (InputStream originInputStream = getClass().getResourceAsStream("/verification/origins/6.jpg");
             InputStream templateInputStream = getClass().getResourceAsStream("/verification/templates/template.png");
             InputStream borderInputStream = getClass().getResourceAsStream("/verification/templates/border.png")
        ) {
            BufferedImage originImage = ImageIO.read(originInputStream);
            BufferedImage templateImage = ImageIO.read(templateInputStream);
            BufferedImage borderImage = ImageIO.read(borderInputStream);

            setRandomBlockPos(info, templateImage);
            generate(info, originImage, templateImage, borderImage);
            // 服务端记录拼图块相关x，y坐标
            String token = UUID.randomUUID().toString();
            cacheMap.put(token, cache(info));
            info.setX(0);
            info.setToken(token);
            return info;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean check(Double x, Double y, String token) {
        VerificationImageInfo info = cacheMap.get(token);
        if (info != null) {
            return info.getX() != null
                    && info.getY() != null
                    && info.getX() + 2 > x
                    && info.getX() - 2 < x
                    && info.getY() + 2 > y
                    && info.getY() - 2 < y;
        }
        return false;
    }

    private VerificationImageInfo cache(VerificationImageInfo verificationImageInfo) {
        VerificationImageInfo cacheInfo = new VerificationImageInfo();
        cacheInfo.setX(verificationImageInfo.getX());
        cacheInfo.setY(verificationImageInfo.getY());
        return cacheInfo;
    }

    /**
     * 生成随机拼图块的位置
     *
     * @param verificationImageInfo 图片信息
     * @param templateImage         模板图片
     */
    private void setRandomBlockPos(VerificationImageInfo verificationImageInfo, BufferedImage templateImage) {
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();
        if (templateImageWidth > DEFAULT_IMAGE_WIDTH || templateImageHeight > DEFAULT_IMAGE_HEIGHT) {
            throw new GlobalServiceException("图片尺寸不符合要求");
        }
        int x = new Random(System.currentTimeMillis()).nextInt(DEFAULT_IMAGE_WIDTH - templateImageWidth);
        int y = new Random(System.currentTimeMillis()).nextInt(DEFAULT_IMAGE_HEIGHT - templateImageHeight);
        verificationImageInfo.setX(x);
        verificationImageInfo.setY(y);
    }

    private void generate(VerificationImageInfo verificationImageInfo, BufferedImage originImage, BufferedImage templateImage, BufferedImage borderImage) {
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();

        // 根据模板图创建一张透明图片
        BufferedImage blockImage = new BufferedImage(templateImageWidth, templateImageHeight, templateImage.getType());
        // 根据随机坐标和模板尺寸从原图裁剪出指定大小的图片
        BufferedImage blockAreaFromOriginImage = getSubImage(originImage, verificationImageInfo.getX(), verificationImageInfo.getY(), templateImageWidth, templateImageHeight, BufferedImage.TYPE_INT_RGB);
        fillBlockArea(templateImage, blockImage, blockAreaFromOriginImage);

        Graphics2D graphics2D = blockImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        // 抗锯齿
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics2D.drawImage(blockImage, 0, 0, null);
        graphics2D.dispose();

        BufferedImage shadeImage = maskOriginImage(originImage, templateImage, verificationImageInfo);

        int[][] borderImageMatrix = getMatrix(borderImage);
        for (int i = 0; i < borderImageMatrix.length; i++) {
            for (int j = 0; j < borderImageMatrix[i].length; j++) {
                int rgb = borderImage.getRGB(i, j);
                if (rgb < 0) {
                    blockImage.setRGB(i, j, -7237488);
                }
            }
        }

        verificationImageInfo.setShadeImage(writeBufferedImage2Base64(shadeImage));
        verificationImageInfo.setBlockImage(writeBufferedImage2Base64(blockImage));
    }

    private void fillBlockArea(BufferedImage templateImage, BufferedImage blockImage, BufferedImage areaOfJigsawPuzzle) {
        int[][] templateImageMatrix = getMatrix(templateImage);
        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[i].length; j++) {
                int rgb = templateImageMatrix[i][j];
                if (rgb < 0) {
                    blockImage.setRGB(i, j, areaOfJigsawPuzzle.getRGB(i, j));
                }
            }
        }
    }

    private BufferedImage maskOriginImage(BufferedImage originImage, BufferedImage templateImage, VerificationImageInfo verificationImageInfo) {
        BufferedImage shadeImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[][] originImageMatrix = getMatrix(originImage);
        int[][] templateImageMatrix = getMatrix(templateImage);

        // 原图像素拷贝到遮罩图
        for (int i = 0; i < originImageMatrix.length; i++) {
            for (int j = 0; j < originImageMatrix[i].length; j++) {
                int rgb = originImage.getRGB(i, j);
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                rgb = r + (g << 8) + (b << 16) + (255 << 24);
                shadeImage.setRGB(i, j, rgb);
            }
        }

        int x = verificationImageInfo.getX();
        int y = verificationImageInfo.getY();

        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[i].length; j++) {
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    int originRGB = shadeImage.getRGB(x + i, y + j);
                    int r = (0xff & originRGB);
                    int g = (0xff & (originRGB >> 8));
                    int b = (0xff & (originRGB >> 16));
                    originRGB = r + (g << 8) + (b << 16) + (140 << 24);
                    shadeImage.setRGB(x + i, y + j, originRGB);
                }
            }
        }
        return shadeImage;
    }

    private int[][] getMatrix(BufferedImage bufferedImage) {
        int[][] matrix = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                matrix[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return matrix;
    }

    private BufferedImage getSubImage(BufferedImage originImage, int x, int y, int width, int height, int imageType) {
        BufferedImage subImage = originImage.getSubimage(x, y, width, height);
        BufferedImage result = new BufferedImage(width, height, imageType);
        Graphics2D graphics2D = result.createGraphics();
        graphics2D.drawImage(subImage, 0, 0, null);
        return result;
    }

    private String writeBufferedImage2Base64(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64Utils.encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
