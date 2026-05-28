CREATE TABLE tb_bank_registry (id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                               name  VARCHAR(255) NOT NULL,
                               compe  VARCHAR(3)   NOT NULL UNIQUE,
                               ispb  VARCHAR(8)   NOT NULL UNIQUE
);

INSERT INTO tb_bank_registry (name, compe, ispb) VALUES
                                                    ('Banco do Brasil', '001', '00000000'),
                                                    ('BNB', '004', '07237373'),
                                                    ('BNDES', '007', '33657248'),
                                                    ('Caixa Economica Federal', '104', '36543902'),
                                                    ('Bradesco', '237', '60746948'),
                                                    ('Itau Unibanco', '341', '60701190'),
                                                    ('Santander', '033', '90400888'),
                                                    ('BTG Pactual', '208', '30306294'),
                                                    ('XP Investimentos', '102', '02332886'),
                                                    ('Nubank', '260', '18236120'),
                                                    ('Inter', '077', '00416968'),
                                                    ('C6 Bank', '336', '31872495'),
                                                    ('PagBank', '290', '08561701'),
                                                    ('Mercado Pago', '323', '10573521'),
                                                    ('Neon', '735', '20855875'),
                                                    ('Banco Safra', '422', '58160789'),
                                                    ('Banco Votorantim', '655', '59588111'),
                                                    ('Banco Original', '212', '92894922'),
                                                    ('Sicoob', '756', '02038232'),
                                                    ('Sicredi', '748', '01181521');