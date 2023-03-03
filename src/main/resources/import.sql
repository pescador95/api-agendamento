
delete
from
	USUARIO U;

select
	SETVAL('usuario_id_seq',
  (
  select
    MAX(ID)
  from
    USUARIO));

delete
from
	AGENDAMENTO A;

select
	SETVAL('agendamento_id_seq',
  (
  select
    MAX(ID)
  from
    AGENDAMENTO A));

delete
from
	PESSOA P;

select
	SETVAL('pessoa_id_seq',
  (
  select
    MAX(ID)
  from
    PESSOA));

delete
from
	PROFILE P;

select
	SETVAL('profile_id_seq',
  (
  select
    MAX(ID)
  from
    PROFILE));

delete
from
	ORGANIZACAO O;

select
	SETVAL('organizacao_id_seq',
  (
  select
    MAX(ID)
  from
    ORGANIZACAO));

delete
from
	ROLE R;

--select
--	SETVAL('role_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    ROLE));
--
--delete
--from
--	USUARIOROLES UR;

--select
--	SETVAL('usuarioroles_id_seq',
--  (
--  select
--    MAX(ID)
--  from
--    USUARIOROLES));

