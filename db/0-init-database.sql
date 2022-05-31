-- 初始化数据库
--
-- 基本库包含：用户、角色和文件等数据表
CREATE
DATABASE `driftwood-cloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Nacos
CREATE
DATABASE `driftwood-cloud-nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Flowable 工作流
CREATE
DATABASE `driftwood-cloud-flowable` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Seata Server
CREATE
DATABASE `driftwood-cloud-seata-server` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Seata Demo
CREATE
DATABASE `driftwood-cloud-seata-account` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Seata Demo
CREATE
DATABASE `driftwood-cloud-seata-order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Seata Demo
CREATE
DATABASE `driftwood-cloud-seata-storage` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
