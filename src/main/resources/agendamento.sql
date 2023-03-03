insert
	into
	AGENDAMENTO (
    ID,
	PESSOAID,
	USERID,
    NOMEPESSOA,
	NOMEPROFISSIONAL,
	STATUSCONSULTA,
	DATAAGENDA,
	ISATIVO,
	DATAACAO,
	ORGANIZACAOID
)
values (
  1,
  1,
  1,
  'João da Silva',
  'Doutor Emmett Brown',
  'AGENDADA',
  NOW(),
  true,
  NOW(),
  1
);