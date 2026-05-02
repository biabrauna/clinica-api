-- ============================================================
-- V3 — Corrige tipo da coluna dataNascimento em CadastroPac
-- Contexto: coluna foi criada como VARCHAR pelo ddl-auto=update
-- antes da adoção de migrations Flyway. O Hibernate (validate)
-- exige DATE. MySQL converte strings YYYY-MM-DD para DATE, mas
-- valores fora deste padrão seriam rejeitados — por isso zeramos
-- os inválidos antes de alterar o tipo.
-- ============================================================

-- Desativa strict mode para esta sessão (evita erro em conversão)
SET SESSION sql_mode = '';

-- Nulifica valores que não seguem o padrão YYYY-MM-DD
UPDATE CadastroPac
SET    dataNascimento = NULL
WHERE  dataNascimento IS NOT NULL
  AND  dataNascimento NOT REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$';

-- Converte a coluna de VARCHAR para DATE
ALTER TABLE CadastroPac
    MODIFY COLUMN dataNascimento DATE NULL;
