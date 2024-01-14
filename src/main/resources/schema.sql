USE batch_tutorial;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS accounts;

-- 주문 테이블 생성
CREATE TABLE `orders` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `order_item` VARCHAR(45) NULL,
     `price` INT NULL,
     `order_date` DATE NULL,
     PRIMARY KEY (`id`))
;

-- 정산 테이블 생성
CREATE TABLE `accounts` (
       `id` INT NOT NULL AUTO_INCREMENT,
       `order_item` VARCHAR(45) NULL,
       `price` INT NULL,
       `order_date` DATE NULL,
       `account_date` DATE NULL,
       PRIMARY KEY (`id`))
;
