package com.icuxika.admin.vo;

public class VerificationImageInfo {

    /**
     * 唯一标识
     */
    private String token;

    /**
     * 拼图位于底图的x轴坐标
     */
    private Integer x;

    /**
     * 拼图位于底图的y轴坐标
     */
    private Integer y;

    /**
     * 底图的Base64编码
     */
    private String shadeImage;

    /**
     * 拼图的Base64编码
     */
    private String blockImage;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getShadeImage() {
        return shadeImage;
    }

    public void setShadeImage(String shadeImage) {
        this.shadeImage = shadeImage;
    }

    public String getBlockImage() {
        return blockImage;
    }

    public void setBlockImage(String blockImage) {
        this.blockImage = blockImage;
    }
}
