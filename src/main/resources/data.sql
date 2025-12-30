INSERT INTO videos (title, description, created_at)
VALUES ('Test video', 'Ovo je test', now());

INSERT INTO users (email,username,password,first_name,last_name,address,enabled,activation_token,created_at)VALUES
    ('pera@gmail.com','pera','$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6','Pera','Peric','Bulevar Oslobodjenja 22',true,NULL, NOW()),
    ('zika@gmail.com','zika','$2a$10$F0WwOocsX7b6Lu.myQ4gNupxGyrj.G.qbqKhaZe1Cr5fgMEEA4kdW','Zika','Zikic','Njegoseva 1',true,NULL, NOW());



