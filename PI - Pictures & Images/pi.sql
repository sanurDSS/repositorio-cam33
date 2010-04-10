drop table if exists fotos;
drop table if exists albumes;
drop table if exists usuarios;
drop table if exists paises;

create table paises
(
	id int auto_increment,
	nombre varchar(100),
	primary key (id),
	unique (nombre)
) engine = innodb default charset=utf8 collate=utf8_spanish_ci;

create table usuarios
(
	id int auto_increment,
	nombre varchar(15) not null,
	contrasena varchar(15) not null,
	email varchar(100),
	sexo enum ('hombre', 'mujer') not null,
	fecha_nacimiento date,
	fecha_registro datetime not null,
	ciudad varchar(100),
	id_pais int,
	primary key (id),
	unique (nombre),
	foreign key (id_pais) references paises (id) on delete restrict on update cascade
) engine = innodb default charset=utf8 collate=utf8_spanish_ci;

create table albumes
(
	id int auto_increment,
	titulo varchar(100),
	descripcion text,
	fecha date,
	id_pais int,
	id_usuario int not null,
	primary key (id),
	foreign key (id_pais) references paises (id) on delete restrict on update cascade,
	foreign key (id_usuario) references usuarios (id) on delete cascade on update cascade
) engine = innodb default charset=utf8 collate=utf8_spanish_ci;

create table fotos
(
	id int auto_increment,
	titulo varchar(100),
	descripcion text,
	nombre_original varchar(100),
	fecha date,
	fecha_registro datetime not null,
	id_pais int,
	id_album int not null,
	primary key (id),
	foreign key (id_pais) references paises (id) on delete restrict on update cascade,
	foreign key (id_album) references albumes (id) on delete cascade on update cascade
) engine = innodb default charset=utf8 collate=utf8_spanish_ci;


insert into paises (nombre) values ('España');
insert into paises (nombre) values ('Francia');
insert into paises (nombre) values ('EE.UU.');
insert into paises (nombre) values ('Chile');
insert into paises (nombre) values ('Japón');
insert into paises (nombre) values ('Italia');
insert into paises (nombre) values ('Inglaterra');
insert into paises (nombre) values ('Alemania');
insert into paises (nombre) values ('Canadá');
insert into paises (nombre) values ('China');
insert into paises (nombre) values ('Portugal');
insert into paises (nombre) values ('Bélgica');
insert into paises (nombre) values ('Dinamarca');
insert into paises (nombre) values ('Argentina');
insert into paises (nombre) values ('Venezuela');
insert into paises (nombre) values ('Brasil');
insert into paises (nombre) values ('México');
insert into paises (nombre) values ('Grecia');
insert into paises (nombre) values ('Rusia');
insert into paises (nombre) values ('Corea del Sur');
insert into paises (nombre) values ('Marruecos');
insert into paises (nombre) values ('Irlanda');
insert into paises (nombre) values ('Escocia');
insert into paises (nombre) values ('Suiza');
insert into paises (nombre) values ('Suecia');
insert into paises (nombre) values ('Australia');
insert into paises (nombre) values ('Tailandia');

insert into usuarios (nombre, contrasena, email, sexo, fecha_nacimiento, fecha_registro, ciudad, id_pais)
values ('cristian', '123456', 'cristian@correo.com', 'hombre', '1987/02/11', now(), 'Elche', 1);
insert into usuarios (nombre, contrasena, email, sexo, fecha_nacimiento, fecha_registro, ciudad, id_pais)
values ('antonio', 'asdf', 'asdf@email.com', 'hombre', '2000/11/07', now(), 'Madrid', 1);


insert into albumes (titulo, descripcion, fecha, id_pais, id_usuario)
values ('Mi primer álbum', 'el primer album que he creado', '2000/10/30', 6, 1);
insert into albumes (titulo, descripcion, fecha, id_pais, id_usuario)
values ('Mi segundo albumcico', 'este fue en otro sitio disitnto a italia', '2010/10/30', 7, 1);

insert into albumes (titulo, descripcion, fecha, id_pais, id_usuario)
values ('Mi primer albumcico de antonio', 'este fue en otro sitio disitnto a italia', '2002/05/19', 9, 2);
insert into albumes (titulo, descripcion, fecha, id_pais, id_usuario)
values ('albumcillo agua mineral bezoya', 'b la bl alb alb alb alb abla bla bla', '1999/09/28', 10, 2);


insert into fotos (titulo, descripcion, nombre_original, fecha, fecha_registro, id_pais, id_album)
values ('mi fotico primera', 'dios, que fundido de añadir descripciones', 'no.set.jpg', '2008/09/16', now(), 6, 1);
insert into fotos (titulo, descripcion, nombre_original, fecha, fecha_registro, id_pais, id_album)
values ('mi segunda primera', 'dios, quasdafa fadsf dsfa asdf asdf', 'nosettampoco.jpg', '2008/09/17', now(), 6, 1);

insert into fotos (titulo, descripcion, nombre_original, fecha, fecha_registro, id_pais, id_album)
values ('fotico de antonio', 'añadir descripciones asdasda sda sda', 'no.set.jpg', '2008/09/16', now(), 10, 4);
insert into fotos (titulo, descripcion, nombre_original, fecha, fecha_registro, id_pais, id_album)
values ('otra focito de antonio', 'qwerty asdasd asda sda sdasdasd', 'nosettampoco.jpg', '1990/09/217', now(), 10, 4);