
INSERT INTO users (email, username, password, first_name, last_name, address, enabled, activation_token, created_at)
VALUES
    ('pera@gmail.com', 'pera', '$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6', 'Pera', 'Peric', 'Bulevar Oslobodjenja 22', true, NULL, NOW()),
    ('zika@gmail.com', 'zika', '$2a$12$NHk/iCm/q0uILyn1MxnKJOpHMwtqpDaMofDsjMrBjFpef1ZN0h/S6', 'Zika', 'Zikic', 'Bulevar Evrope 2', true, NULL, NOW()),
    ('mika@gmail.com', 'mika', '$2a$12$Ce2aaKbS1EyHZf8slKfN1es6c/wVbFZEEtOXvyNaqWknlICUzEJ9e', 'Mika', 'Mikic', 'Cara Dusana 10', true, NULL, NOW()),
    ('ana@gmail.com', 'ana', '$2a$12$oZvno7SkAQK0vhBP8tnpVOWvj/gdC/PNqBVehtBSQpDe5.cwY5xcW', 'Ana', 'Anic', 'Bulevar Mihajla Pupina 7', true, NULL, NOW()),
    ('lena@gmail.com', 'lena', '$2a$12$3bFR9DAyXLgW/0C0Z1/cgeNFFCGgfdJ9uRbcJ61ksq.6/EUnFZRd2', 'Lena', 'Lenic', 'Jevrejska 3', true, NULL, NOW());


INSERT INTO videos (
    title, description, video_path, thumbnail_path, user_id, created_at, view_count,
    scheduled, scheduled_at, location, latitude, longitude, size_mb
)
VALUES
    ('Nauci da sviras klavir', 'opis',
     'storage\\videos\\14247790-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\klavir.jpg',
     3,
     NOW() - INTERVAL '5 days', 0,
     false, NULL,
     'Beograd', 44.7866, 20.4489, 30),

    ('Umirujuce more', 'Letnji video sa mora koji prikazuje plažu, talase i opuštenu atmosferu. Kratak klip koji dočarava letnji odmor i sunce.',
     'storage\\videos\\855633-hd_1920_1080_25fps.mp4',
     'storage\\thumbnails\\gradska_plaza2.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours', 0,
     false, NULL,
     'Nis', 43.3209, 21.8958, 20),

    ('Poslovni razgovor', 'Još jedan video',
     'storage\\videos\\5725960-uhd_3840_2160_30fps.mp4',
     'storage\\thumbnails\\kancelarija.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours', 47,
     false, NULL,
     'Novi Sad', 45.2671, 19.8335, 25),

    ('Trening kod kuce', 'Još jedan video',
     'storage\\videos\\293085_small.mp4',
     'storage\\thumbnails\\trening.jpg',
     4,
     NOW() - INTERVAL '3 days', 0,
     false, NULL,
     'Beograd', 44.7866, 20.4489, 29),

    ('Priprema hrane', 'Jednostavan video pripreme hrane uz praktične savete i brz proces. Ideje za svakodnevne obroke i laganu kuhinju.',
     'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\tiganj.jpg',
     2,
     NOW() - INTERVAL '2 days 6 hours', 0,
     false, NULL,
     'Kragujevac', 44.0128, 20.9114, 12),

    ('Potcast', 'Opušten razgovor u podcast studiju na zanimljivu temu.',
     'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4',
     'storage\\thumbnails\\potcast1.jpg',
     5,
     NOW() - INTERVAL '1 day', 0,
     false, NULL,
     'Novi Sad', 45.2671, 19.8335, 15),

    ('Voznja bicikla', 'Još jedan video',
     'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4',
     'storage\\thumbnails\\bicikli.jpg',
     1,
     NOW() - INTERVAL '20 hours', 12,
     false, NULL,
     'Novi Sad', 45.2671, 19.8335, 4),

    ('Kako nasminkati oci', 'Još jedan video',
     'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\sminka.jpg',
     2,
     NOW() - INTERVAL '8 hours', 0,
     false, NULL,
     'Beograd', 44.7866, 20.4489, 12),

    ('Skijanje', 'Kratak video sa skijanja koji prikazuje zimsku atmosferu, sneg i uživanje na stazi. Savršen prikaz zimskog odmora i adrenalina.',
     'storage\\videos\\4274798-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\skijanje.jpg',
     3,
     NOW() - INTERVAL '1 hour', 32,
     false, NULL,
     'Novi Sad', 45.2671, 19.8335, 32),

    ('Bozicna atmosfera', 'Topla praznična atmosfera uz svetla, ukrase i božićni duh. Kratak video koji dočarava mir i radost praznika.',
     'storage\\videos\\855167-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\bozic.jpeg',
     2,
     NOW() - INTERVAL '1 hour', 126,
     false, NULL,
     'Subotica', 46.1005, 19.6656, 27),

    ('Avion', 'Pogled iz aviona tokom poletanja i sletanja, uz osećaj putovanja i slobode. Kratak klip savršen za ljubitelje avijacije.',
     'storage\\videos\\3678380-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\avion.jpg',
     2,
     NOW() - INTERVAL '1 hour', 100,
     false, NULL,
     'Beograd (Aerodrom)', 44.8184, 20.3091, 22),

    ('Ajfelov toranj', 'Kratak video čuvenog Ajfelovog tornja u Parizu, snimljen iz grada. Ikona romantike i putovanja.',
     'storage\\videos\\6355353-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\paris.jpg',
     4,
     NOW() - INTERVAL '1 hour', 254,
     false, NULL,
     'Pariz', 48.8566, 2.3522, 34);




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


INSERT INTO comments (text, video_id, user_id, created_at) VALUES
                                                               ('Top video!', 1, 1, NOW()),
                                                               ('Brutalno!', 1, 2, NOW()),
                                                               ('Nice', 2, 2, NOW()),
                                                               ('Lep video i dobra atmosfera.', 2, 3, NOW()),
                                                               ('Bas opustajuce.', 2, 4, NOW()),


                                                               ('Koristan primer razgovora.', 3, 5, NOW()),
                                                               ('Dobar savet za intervju.', 3, 3, NOW()),


                                                               ('Super trening za kucu.', 4, 4, NOW()),
                                                               ('Bas me je umorio.', 4, 5, NOW()),


                                                               ('Brz i jednostavan recept.', 5, 3, NOW()),
                                                               ('Ovo cu probati veceras.', 5, 4, NOW()),


                                                               ('Zanimljiva tema.', 6, 5, NOW()),
                                                               ('Prijatno za slusanje.', 6, 3, NOW()),


                                                               ('Dobra voznja i lep kadar.', 7, 4, NOW()),
                                                               ('Super ruta.', 7, 5, NOW()),


                                                               ('Lepo objasnjeni koraci.', 8, 3, NOW()),
                                                               ('Korisni saveti.', 8, 4, NOW()),


                                                               ('Odlicni kadrovi.', 9, 5, NOW()),
                                                               ('Bas izgleda adrenalinski.', 9, 3, NOW()),


                                                               ('Bas praznicna atmosfera.', 10, 4, NOW()),
                                                               ('Lepo i mirno.', 10, 5, NOW()),


                                                               ('Lep pogled iz aviona.', 11, 3, NOW()),
                                                               ('Zanimljivo snimljeno.', 11, 4, NOW()),


                                                               ('Pariz uvek izgleda lepo.', 12, 5, NOW()),
                                                               ('Dobar ugao snimanja.', 12, 3, NOW());


INSERT INTO video_likes (video_id, user_id, created_at) VALUES

(2, 1, NOW()),
(2, 3, NOW()),


(3, 2, NOW()),
(3, 4, NOW()),


(4, 3, NOW()),
(4, 5, NOW()),


(5, 1, NOW()),
(5, 4, NOW()),


(6, 2, NOW()),
(6, 5, NOW()),


(7, 3, NOW()),
(7, 4, NOW()),


(8, 1, NOW()),
(8, 5, NOW()),


(9, 2, NOW()),
(9, 3, NOW()),


(10, 4, NOW()),
(10, 5, NOW()),


(11, 1, NOW()),
(11, 3, NOW()),


(12, 2, NOW()),
(12, 4, NOW());

