create database BankingDB;
use BankingDB;
 
create table customer_data(
customer_id int primary key auto_increment,
fname varchar(30) not null,
mname varchar(3) not null,
lname varchar(30) not null,
contactnumber varchar(11) not null,
email varchar(100) not null,
address varchar(1000) not null,
birthdate date not null
);
insert into customer_data
values(1,'Juan','D','Cruz','09123456789','sample.email@gmail.com','Street, City, Province','yyyy-mm-dd');
 
select * from customer_data;
 
drop table customer_data;
 
create table account_data(
account_id int primary key auto_increment,
account_pass varchar(100) not null,
customer_id int not null,
branch_id int not null,
balance numeric(12,2) not null,
constraint customer_fk foreign key(customer_id) references customer_data(customer_id),
constraint branch_fk foreign key(branch_id) references branch_data(branch_id)
);
insert into account_data
values(1,'123',1,3000,120000);
 
select * from account_data;
drop table account_data;
 
create table branch_data(
branch_id int primary key,
branch_name varchar(50) not null,
city varchar(1000) not null
);
 
insert into branch_data
values(3000,'BANKING SYSTEM','City');
 
select * from branch_data;
 
create table transaction_table(
transaction_id varchar(7) primary key,
account_id int not null,
reciever varchar(50) not null,
transaction_date timestamp not null,
transaction_type varchar(20) not null,
transaction_amount float not null,
balance numeric(12,2) not null,
constraint account_fk foreign key(account_id) references account_data(account_id)
);
 
select * from transaction_table;
drop table transaction_table;
