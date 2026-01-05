-- ===== USERS =====
INSERT INTO users (email, username, password, first_name, last_name, address, enabled, activation_token, created_at)
VALUES
    ('pera@gmail.com', 'pera', '$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6', 'Pera', 'Peric', 'Bulevar Oslobodjenja 22', true, NULL, NOW()),
    ('zika@gmail.com', 'zika', '$2a$12$NHk/iCm/q0uILyn1MxnKJOpHMwtqpDaMofDsjMrBjFpef1ZN0h/S6', 'Zika', 'Zikic', 'Bulevar Evrope 2', true, NULL, NOW());



-- ===== VIDEOS (BEZ tag kolone!) =====
INSERT INTO videos (title, description, video_path, thumbnail_path, user_id, created_at, view_count)
VALUES
    ('Nauci da sviras klavir', 'opis',
     'storage\\videos\\14247790-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\klavir.jpg',
     1,
     NOW() - INTERVAL '5 days', 0),

    ('Umirujuce more', 'Letnji video sa mora koji prikazuje plažu, talase i opuštenu atmosferu. Kratak klip koji dočarava letnji odmor i sunce.',
     'storage\\videos\\855633-hd_1920_1080_25fps.mp4',
     'storage\\thumbnails\\gradska_plaza2.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours', 0),

    ('Poslovni razgovor', 'Još jedan video',
     'storage\\videos\\5725960-uhd_3840_2160_30fps.mp4',
     'storage\\thumbnails\\kancelarija.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours', 47),

    ('Trening kod kuce', 'Još jedan video',
     'storage\\videos\\293085_small.mp4',
     'storage\\thumbnails\\trening.jpg',
     2,
     NOW() - INTERVAL '3 days', 0),

    ('Priprema hrane', 'Jednostavan video pripreme hrane uz praktične savete i brz proces. Ideje za svakodnevne obroke i laganu kuhinju.',
     'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\tiganj.jpg',
     2,
     NOW() - INTERVAL '2 days 6 hours', 0),

    ('Potcast', 'Opušten razgovor u podcast studiju na zanimljivu temu.',
     'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4',
     'storage\\thumbnails\\potcast1.jpg',
     1,
     NOW() - INTERVAL '1 day', 0),

    ('Voznja bicikla', 'Još jedan video',
     'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4',
     'storage\\thumbnails\\bicikli.jpg',
     1,
     NOW() - INTERVAL '20 hours', 12),

    ('Kako nasminkati oci', 'Još jedan video',
     'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\sminka.jpg',
     2,
     NOW() - INTERVAL '8 hours', 0),

    ('Skijanje', 'Kratak video sa skijanja koji prikazuje zimsku atmosferu, sneg i uživanje na stazi. Savršen prikaz zimskog odmora i adrenalina.',
     'storage\\videos\\4274798-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\skijanje.jpg',
     2,
     NOW() - INTERVAL '1 hour', 32),

    ('Bozicna atmosfera', 'Topla praznična atmosfera uz svetla, ukrase i božićni duh. Kratak video koji dočarava mir i radost praznika.',
     'storage\\videos\\855167-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\bozic.jpeg',
     2,
     NOW() - INTERVAL '1 hour', 126),

    ('Avion', 'Pogled iz aviona tokom poletanja i sletanja, uz osećaj putovanja i slobode. Kratak klip savršen za ljubitelje avijacije.',
     'storage\\videos\\3678380-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\avion.jpg',
     2,
     NOW() - INTERVAL '1 hour', 100),

    ('Ajfelov toranj', 'Kratak video čuvenog Ajfelovog tornja u Parizu, snimljen iz grada. Ikona romantike i putovanja.',
     'storage\\videos\\6355353-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\paris.jpg',
     2,
     NOW() - INTERVAL '1 hour', 254);



-- ===== TAGOVI (ElementCollection tabela: video_tags) =====
-- video_id pretpostavka: redosled insert-a daje id 1..12

INSERT INTO video_tags (video_id, tag) VALUES
                                           (1, 'piano'),
                                           (1, 'music'),

                                           (2, 'summer'),

                                           (3, 'office'),
                                           (3, 'work'),

                                           (4, 'healthy'),
                                           (4, 'trening'),
                                           (4, 'odkuce'),

                                           (5, 'ukusno'),
                                           (5, 'brzo'),
                                           (5, 'hrana'),

                                           (6, 'talking'),
                                           (6, 'people'),
                                           (6, 'potcast'),

                                           (7, 'bajs'),
                                           (7, 'voznja'),

                                           (8, 'sminka'),

                                           (9, 'snow'),
                                           (9, 'zima'),
                                           (9, 'adrenalin'),

                                           (10, 'home'),
                                           (10, 'jelka'),
                                           (10, 'bozic'),

                                           (11, 'sletanje'),
                                           (11, 'avion'),

                                           (12, 'paris'),
                                           (12, 'romance');



-- ===== KOMENTARI =====
INSERT INTO comments (text, video_id, user_id, created_at)
VALUES
    ('Top video!', 1, 1, NOW()),
    ('Brutalno!', 1, 2, NOW()),
    ('Nice', 2, 2, NOW());
-- dodatni komentari za video 1
INSERT INTO comments (text, video_id, user_id, created_at) VALUES
                                                               ('Komentar 1',  1, 1, NOW() - INTERVAL '60 minutes'),
                                                               ('Komentar 2',  1, 2, NOW() - INTERVAL '59 minutes'),
                                                               ('Komentar 3',  1, 1, NOW() - INTERVAL '58 minutes'),
                                                               ('Komentar 4',  1, 2, NOW() - INTERVAL '57 minutes'),
                                                               ('Komentar 5',  1, 1, NOW() - INTERVAL '56 minutes'),

                                                               ('Komentar 6',  1, 2, NOW() - INTERVAL '55 minutes'),
                                                               ('Komentar 7',  1, 1, NOW() - INTERVAL '54 minutes'),
                                                               ('Komentar 8',  1, 2, NOW() - INTERVAL '53 minutes'),
                                                               ('Komentar 9',  1, 1, NOW() - INTERVAL '52 minutes'),
                                                               ('Komentar 10', 1, 2, NOW() - INTERVAL '51 minutes'),

                                                               ('Komentar 11', 1, 1, NOW() - INTERVAL '50 minutes'),
                                                               ('Komentar 12', 1, 2, NOW() - INTERVAL '49 minutes'),
                                                               ('Komentar 13', 1, 1, NOW() - INTERVAL '48 minutes'),
                                                               ('Komentar 14', 1, 2, NOW() - INTERVAL '47 minutes'),
                                                               ('Komentar 15', 1, 1, NOW() - INTERVAL '46 minutes'),

                                                               ('Komentar 16', 1, 2, NOW() - INTERVAL '45 minutes'),
                                                               ('Komentar 17', 1, 1, NOW() - INTERVAL '44 minutes'),
                                                               ('Komentar 18', 1, 2, NOW() - INTERVAL '43 minutes'),
                                                               ('Komentar 19', 1, 1, NOW() - INTERVAL '42 minutes'),
                                                               ('Komentar 20', 1, 2, NOW() - INTERVAL '41 minutes');




-- ===== LAJKOVI =====
INSERT INTO video_likes (video_id, user_id, created_at)
VALUES
    (1, 1, NOW()),
    (1, 2, NOW()),
    (2, 2, NOW());
