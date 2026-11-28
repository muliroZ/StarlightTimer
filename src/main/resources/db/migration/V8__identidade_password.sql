ALTER TABLE identidade ALTER COLUMN provedor SET NOT NULL;

ALTER TABLE identidade ADD CONSTRAINT provedor_check CHECK (provedor IN ('PASSWORD'));

ALTER TABLE identidade ADD CONSTRAINT identidade_password_check
    CHECK (provedor <> 'PASSWORD' OR (senha_hash IS NOT NULL AND id_externo IS NULL));

ALTER TABLE identidade ADD CONSTRAINT unique_usuario_provedor UNIQUE (usuario_id, provedor);
