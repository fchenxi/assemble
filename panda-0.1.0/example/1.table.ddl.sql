#-------------------------------------------------------------------------------
#  客户端各表结构脚本
#  version: phoenix-api-0.1.0
#
#  MODIFIED     (MM/DD/YY) 
#  chase.xi     07/11/16  - 修改模板
#
#-------------------------------------------------------------------------------

#创建销售明细表
SET FOREIGN_KEY_CHECKS=0;
-- -------------------------------
-- Table structure for miu_sales_detail
-- -------------------------------
DROP TABLE IF EXISTS miu_sales_detail;
CREATE TABLE miu_sales_detail (
  id varchar(32) NOT NULL COMMENT 'id',
  brand varchar(32) NOT NULL COMMENT 'the name of the brand',
  price varchar(32) NOT NULL COMMENT 'the brand price',
  sales_total DATE DEFAULT NULL COMMENT 'the brand sales total',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update of the time',
  CONSTRAINT pk PRIMARY KEY (`id`)
);


create table miu_sales_detail (
  id varchar(32) not null,
  brand varchar(32) not null,
  price integer,
  sales_total integer,
  CONSTRAINT pk PRIMARY KEY (id, brand)
);

upsert into miu_sales_detail values ('11111122222223', 'belle', 250, 25000);
upsert into miu_sales_detail values ('11111122222224', 'tata', 260, 26000);
