DROP DATABASE IF EXISTS `JAM`;
CREATE DATABASE `JAM`;
USE `JAM`;

CREATE TABLE article(
    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    title CHAR(100) NOT NULL,
    `body` TEXT NOT NULL
);

SHOW TABLES;
DESC article;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
title = CONCAT('제목 ',RAND()),
`body` = CONCAT('내용 ',RAND());

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
title = '제목 112',
`body` = '내용 12';

SELECT * 
FROM article;

UPDATE article
SET updateDate = NOW(),
title = '제목2'
WHERE id = 2;