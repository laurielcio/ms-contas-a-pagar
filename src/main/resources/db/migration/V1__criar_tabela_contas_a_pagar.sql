CREATE TABLE contas_a_pagar (
                                id SERIAL PRIMARY KEY,
                                data_pagamento DATE,
                                data_vencimento DATE NOT NULL,
                                valor NUMERIC(10, 2) NOT NULL,
                                descricao VARCHAR(255) NOT NULL,
                                situacao VARCHAR(50) NOT NULL
);