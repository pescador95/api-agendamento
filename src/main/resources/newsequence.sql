
select
	SETVAL('agendamento_id_seq',
  (
  select
    MAX(ID)
  from
    AGENDAMENTO));

select
	SETVAL('usuario_id_seq',
  (
  select
    MAX(ID)
  from
    USUARIO));

select
	SETVAL('pessoa_id_seq',
  (
  select
    MAX(ID)
  from
    PESSOA));

select
	SETVAL('organizacao_id_seq',
  (
  select
    MAX(ID)
  from
    ORGANIZACAO));

--select
--	SETVAL('profile_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    PROFILE));
--
--select
--	SETVAL('role_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    ROLE));

--select
--	SETVAL('usuarioroles_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    USUARIOROLES));