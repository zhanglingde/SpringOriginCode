create table account(
    id    int(11) not null primary key auto_increment,
    name  varchar(20),
    money float(10, 2)
);

INSERT INTO `test`.`account` (`id`, `name`, `money`) VALUES (1, 'zhang', 100.00);
INSERT INTO `test`.`account` (`id`, `name`, `money`) VALUES (2, 'lisi', 200.00);
