insert into SystemUser (emailaddress,enabled,username,userpassword,adminuser) values ('a@b.hu',TRUE,'Papp Zoltán', 'jjXCzTv2ZBvbDiBQt2kyy7LmA0oN2swdm+qCprpX988=',TRUE);
insert into user_join_group (group_name,user_name) values ('ROLE_ADMIN','a@b.hu');

CREATE VIEW v_active_user AS select u.emailAddress AS username, u.userpassword AS password from systemuser u where u.enabled = true;

insert into SubjectType (id,name) values (-1,'találkozó');
insert into SubjectType (id,name) values (1,'étkezés');
insert into SubjectType (id,name) values (2,'ügyintézés');
insert into SubjectType (id,name) values (3,'mentális tanácsadás');
insert into SubjectType (id,name) values (4,'szabadidős program');
insert into SubjectType (id,name) values (5,'egyéb');

insert into AccessibleType (id,typename) values (1,'cím');
insert into AccessibleType (id,typename) values (2,'telefon');
insert into AccessibleType (id,typename) values (3,'mobil');