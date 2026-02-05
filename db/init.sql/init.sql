BEGIN;

-- =========================
-- 1) TABLES (SCHEMA)
-- =========================

CREATE TABLE IF NOT EXISTS users (
                                     id              BIGSERIAL PRIMARY KEY,
                                     email           VARCHAR(255) NOT NULL UNIQUE,
    username        VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    address         VARCHAR(255),
    enabled         BOOLEAN NOT NULL DEFAULT FALSE,
    activation_token VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS videos (
                                      id                        BIGSERIAL PRIMARY KEY,
                                      title                     VARCHAR(255) NOT NULL,
    description               TEXT NOT NULL,
    tags                      TEXT, -- (ne koristiš direktno; stvarni tagovi su u video_tags)
    thumbnail_path            VARCHAR(255),
    thumbnail_compressed      BOOLEAN NOT NULL DEFAULT FALSE,
    thumbnail_compressed_path VARCHAR(255),

    video_path                VARCHAR(255),
    size_mb                   BIGINT,
    created_at                TIMESTAMP,
    location                  VARCHAR(255),
    latitude                  DOUBLE PRECISION,
    longitude                 DOUBLE PRECISION,
    geohash                   VARCHAR(12),
    view_count                BIGINT NOT NULL DEFAULT 0,
    scheduled                 BOOLEAN NOT NULL DEFAULT FALSE,
    scheduled_at              TIMESTAMP,
    premiere_ended            BOOLEAN NOT NULL DEFAULT FALSE,
    duration_seconds          BIGINT,
    transcode_status          VARCHAR(20) NOT NULL DEFAULT 'UPLOADED',
    transcoded_path           VARCHAR(255),

    user_id                   BIGINT,
    CONSTRAINT fk_videos_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- ElementCollection: video_tags(video_id, tag)
CREATE TABLE IF NOT EXISTS video_tags (
                                          video_id BIGINT NOT NULL,
                                          tag      VARCHAR(255) NOT NULL,
    CONSTRAINT fk_video_tags_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments (
                                        id         BIGSERIAL PRIMARY KEY,
                                        text       TEXT NOT NULL,
                                        video_id   BIGINT NOT NULL,
                                        user_id    BIGINT NOT NULL,
                                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_comments_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS video_likes (
                                           id         BIGSERIAL PRIMARY KEY,
                                           video_id   BIGINT NOT NULL,
                                           user_id    BIGINT NOT NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_video_likes_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    CONSTRAINT fk_video_likes_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    CONSTRAINT uq_video_likes UNIQUE (video_id, user_id)
    );



-- =========================
-- 2) DATA (INSERTS)
-- =========================

INSERT INTO users (email, username, password, first_name, last_name, address, enabled, activation_token, created_at)
VALUES
    ('pera@gmail.com', 'pera', '$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6', 'Pera', 'Peric', 'Bulevar Oslobodjenja 22', true, NULL, NOW()),
    ('zika@gmail.com', 'zika', '$2a$12$NHk/iCm/q0uILyn1MxnKJOpHMwtqpDaMofDsjMrBjFpef1ZN0h/S6', 'Zika', 'Zikic', 'Bulevar Evrope 2', true, NULL, NOW()),
    ('mika@gmail.com', 'mika', '$2a$12$Ce2aaKbS1EyHZf8slKfN1es6c/wVbFZEEtOXvyNaqWknlICUzEJ9e', 'Mika', 'Mikic', 'Cara Dusana 10', true, NULL, NOW()),
    ('ana@gmail.com', 'ana', '$2a$12$oZvno7SkAQK0vhBP8tnpVOWvj/gdC/PNqBVehtBSQpDe5.cwY5xcW', 'Ana', 'Anic', 'Bulevar Mihajla Pupina 7', true, NULL, NOW()),
    ('lena@gmail.com', 'lena', '$2a$12$3bFR9DAyXLgW/0C0Z1/cgeNFFCGgfdJ9uRbcJ61ksq.6/EUnFZRd2', 'Lena', 'Lenic', 'Jevrejska 3', true, NULL, NOW()),
    ('marko@gmail.com',  'marko',  '$2a$12$9r2q9bRYWzk1PJfLpsIP6ucFQQ/T7.YiN66k9peJ1fAC9unLCIv3O', 'Marko',  'Markovic',  'Narodnog fronta 12', true, NULL, NOW()),
    ('jelena@gmail.com', 'jelena', '$2a$12$/paUejfcPsDMYdUXh3ia4eQhEuC3rIfYV2XMLaM08EwT9Z5psIzVa', 'Jelena', 'Jovanovic', 'Bulevar Evrope 45',   true, NULL, NOW()),
    ('nikola@gmail.com', 'nikola', '$2a$12$332W5Lzr9r5we.yV2wRfhe0wY6m4ZHzZjkWWTKIOhIkQ9Dd3jYZgq', 'Nikola', 'Nikolic',   'Kralja Petra 3',      true, NULL, NOW()),
    ('milica@gmail.com', 'milica', '$2a$12$Sz6yxxqrodICXrp4Wnycm.G4rxXWnBJ7h0CkF3y0ve4KFZzlJbrM2', 'Milica', 'Milic',     'Futoska 88',          true, NULL, NOW()),
    ('stefan@gmail.com', 'stefan', '$2a$12$Rw2JcH/rOuxv7qEGn/d8wOsh2B4s6cB8O5R1Ob6kHsLF6hhF.Eylq', 'Stefan', 'Stefanovic','Cara Lazara 20',      true, NULL, NOW()),
    ('iva@gmail.com',    'iva',    '$2a$12$k5p7DbAUqIEKZ1hUZZzI3uXVn1xBaUtEDJUQsba3wlu4OWE9eGlPC', 'Iva',    'Ivic',      'Bulevar Mihajla Pupina 19', true, NULL, NOW()),
    ('danilo@gmail.com', 'danilo', '$2a$12$YQ61pMGnfwD2kymzujD0R.bOobCaDraZi5EiNtzx1c2WX1wvWh/Q.', 'Danilo', 'Danic',     'Jevrejska 14',        true, NULL, NOW()),
    ('tamara@gmail.com', 'tamara', '$2a$12$Iu8BmuP/ODCp6M.VooePvOjx0qSsRkHdC9kydzV/i4voDtod.5loK', 'Tamara', 'Tomic',     'Gunduliceva 6',       true, NULL, NOW()),
    ('nemanja@gmail.com','nemanja','$2a$12$uXjYDInBUwF.yMLIc36DVOJI1ULaBz010SS0OTJYiwDq/h.QeyOuy', 'Nemanja','Nenic',     'Bulevar Oslobodjenja 101', true, NULL, NOW()),
    ('sofija@gmail.com', 'sofija', '$2a$12$MBachD90tVQvtAsufyznGe3Bc30h7FWRwutqyXg4cOoTHtXu9Oi1m', 'Sofija', 'Sofric',    'Zmaj Jovina 2',       true, NULL, NOW());

INSERT INTO videos (
    title, description, video_path, thumbnail_path, user_id,
    created_at, view_count,
    scheduled, scheduled_at,
    location, latitude, longitude,
    size_mb,
    premiere_ended, duration_seconds,
    transcode_status, transcoded_path,
    thumbnail_compressed, thumbnail_compressed_path
)
VALUES
    (
        'Nauci da sviras klavir', 'opis',
        'storage\\videos\\14247790-hd_1920_1080_30fps.mp4',
        'storage\\thumbnails\\klavir.jpg',
        3,
        NOW() - INTERVAL '5 days', 34,
        false, NULL,
        'Beograd', 44.7866, 20.4489,
        30,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Umirujuce more',
        'Letnji video sa mora koji prikazuje plažu, talase i opuštenu atmosferu. Kratak klip koji dočarava letnji odmor i sunce.',
        'storage\\videos\\855633-hd_1920_1080_25fps.mp4',
        'storage\\thumbnails\\gradska_plaza2.jpg',
        1,
        NOW() - INTERVAL '4 days 3 hours', 15,
        false, NULL,
        'Nis', 43.3209, 21.8958,
        20,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Poslovni razgovor', 'Još jedan video',
        'storage\\videos\\5725960-uhd_3840_2160_30fps.mp4',
        'storage\\thumbnails\\kancelarija.jpg',
        1,
        NOW() - INTERVAL '4 days 3 hours', 47,
        false, NULL,
        'Novi Sad', 45.2671, 19.8335,
        25,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Trening kod kuce', 'Još jedan video',
        'storage\\videos\\293085_small.mp4',
        'storage\\thumbnails\\trening.jpg',
        4,
        NOW() - INTERVAL '3 days', 7,
        false, NULL,
        'Beograd', 44.7866, 20.4489,
        29,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Priprema hrane',
        'Jednostavan video pripreme hrane uz praktične savete i brz proces. Ideje za svakodnevne obroke i laganu kuhinju.',
        'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4',
        'storage\\thumbnails\\tiganj.jpg',
        2,
        NOW() - INTERVAL '2 days 6 hours', 22,
        false, NULL,
        'Kragujevac', 44.0128, 20.9114,
        12,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Potcast',
        'Opušten razgovor u podcast studiju na zanimljivu temu.',
        'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4',
        'storage\\thumbnails\\potcast1.jpg',
        5,
        NOW() - INTERVAL '1 day', 204,
        false, NULL,
        'Novi Sad', 45.2671, 19.8335,
        15,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Voznja bicikla', 'Još jedan video',
        'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4',
        'storage\\thumbnails\\bicikli.jpg',
        1,
        NOW() - INTERVAL '20 hours', 12,
        false, NULL,
        'Novi Sad', 45.2671, 19.8335,
        4,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Kako nasminkati oci', 'Još jedan video',
        'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4',
        'storage\\thumbnails\\sminka.jpg',
        2,
        NOW() - INTERVAL '8 hours', 57,
        false, NULL,
        'Beograd', 44.7866, 20.4489,
        12,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Skijanje',
        'Kratak video sa skijanja koji prikazuje zimsku atmosferu, sneg i uživanje na stazi. Savršen prikaz zimskog odmora i adrenalina.',
        'storage\\videos\\4274798-uhd_3840_2160_25fps.mp4',
        'storage\\thumbnails\\skijanje.jpg',
        3,
        NOW() - INTERVAL '1 hour', 32,
        false, NULL,
        'Novi Sad', 45.2671, 19.8335,
        32,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Bozicna atmosfera',
        'Topla praznična atmosfera uz svetla, ukrase i božićni duh. Kratak video koji dočarava mir i radost praznika.',
        'storage\\videos\\855167-hd_1920_1080_30fps.mp4',
        'storage\\thumbnails\\bozic.jpeg',
        2,
        NOW() - INTERVAL '1 hour', 126,
        false, NULL,
        'Subotica', 46.1005, 19.6656,
        27,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Avion',
        'Pogled iz aviona tokom poletanja i sletanja, uz osećaj putovanja i slobode. Kratak klip savršen za ljubitelje avijacije.',
        'storage\\videos\\3678380-hd_1920_1080_30fps.mp4',
        'storage\\thumbnails\\avion.jpg',
        2,
        NOW() - INTERVAL '1 hour', 100,
        false, NULL,
        'Beograd (Aerodrom)', 44.8184, 20.3091,
        22,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    ),
    (
        'Ajfelov toranj',
        'Kratak video čuvenog Ajfelovog tornja u Parizu, snimljen iz grada. Ikona romantike i putovanja.',
        'storage\\videos\\6355353-hd_1920_1080_30fps.mp4',
        'storage\\thumbnails\\paris.jpg',
        4,
        NOW() - INTERVAL '1 hour', 254,
        false, NULL,
        'Pariz', 48.8566, 2.3522,
        34,
        false, NULL,
        'TRANSCODING', NULL,
        false, NULL
    );

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
                                                               ('Prelepa muzika, baš prija.', 1, 6, NOW() - INTERVAL '2 days'),
                                                               ('Koji je ovo model klavira?', 1, 7, NOW() - INTERVAL '1 day 6 hours'),
                                                               ('Svaka čast na objašnjenju!', 1, 8, NOW() - INTERVAL '10 hours'),

                                                               ('Nice', 2, 2, NOW()),
                                                               ('Lep video i dobra atmosfera.', 2, 3, NOW()),
                                                               ('Bas opustajuce.', 2, 4, NOW()),
                                                               ('Ovo me je vratilo na more', 2, 9, NOW() - INTERVAL '3 days'),
                                                               ('Kadrovi su brutalni!',       2, 10, NOW() - INTERVAL '2 days 2 hours'),
                                                               ('Top atmosfera!',             2, 11, NOW() - INTERVAL '18 hours'),
                                                               ('Mogao bi duži klip!',        2, 12, NOW() - INTERVAL '7 hours'),

                                                               ('Koristan primer razgovora.', 3, 5, NOW()),
                                                               ('Dobar savet za intervju.', 3, 3, NOW()),
                                                               ('Ovo realno pomaže za intervju.', 3, 6, NOW() - INTERVAL '2 days 5 hours'),

                                                               ('Super trening za kucu.', 4, 4, NOW()),
                                                               ('Bas me je umorio.', 4, 5, NOW()),
                                                               ('Konačno trening bez opreme.', 4, 8, NOW() - INTERVAL '2 days'),

                                                               ('Brz i jednostavan recept.', 5, 3, NOW()),
                                                               ('Ovo cu probati veceras.', 5, 4, NOW()),
                                                               ('Pravim ovo sutra ',        5, 6, NOW() - INTERVAL '1 day 10 hours'),
                                                               ('Jednostavno i ukusno.',      5, 9, NOW() - INTERVAL '1 day 1 hour'),
                                                               ('Odličan recept!',            5, 12, NOW() - INTERVAL '4 hours'),

                                                               ('Zanimljiva tema.', 6, 5, NOW()),
                                                               ('Prijatno za slusanje.', 6, 3, NOW()),
                                                               ('Podcast je baš opuštajući.', 6, 7, NOW() - INTERVAL '23 hours'),
                                                               ('Ko je gost?',                6, 8, NOW() - INTERVAL '12 hours'),
                                                               ('Tema je baš zanimljiva.',    6, 15, NOW() - INTERVAL '2 hours'),
                                                               ('Interesantno.', 6, 7, NOW() - INTERVAL '23 hours'),
                                                               ('Izbor gosta odlican.?', 6, 14, NOW() - INTERVAL '12 hours'),
                                                               ('Ekstra.', 6, 13, NOW() - INTERVAL '2 hours'),

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
                                                               ('Dobar ugao snimanja.', 12, 3, NOW()),

                                                               ('Ovo je ludilo ',           9, 6, NOW() - INTERVAL '50 minutes'),
                                                               ('Adrenalin na max!',        9, 8, NOW() - INTERVAL '40 minutes'),
                                                               ('Prelepa zimska scena.',    9, 10, NOW() - INTERVAL '25 minutes'),

                                                               ('Baš praznično!',           10, 6, NOW() - INTERVAL '55 minutes'),
                                                               ('Sviđa mi se montaža.',     10, 7, NOW() - INTERVAL '45 minutes'),

                                                               ('Top snimak!',              11, 8, NOW() - INTERVAL '35 minutes'),
                                                               ('Kakav pogled ',            11, 9, NOW() - INTERVAL '20 minutes'),

                                                               ('Pariz je uvek dobar izbor.', 12, 6, NOW() - INTERVAL '30 minutes'),
                                                               ('Odličan klip!',              12, 10, NOW() - INTERVAL '10 minutes');

INSERT INTO video_likes (video_id, user_id, created_at) VALUES
                                                            (1, 1, NOW()),
                                                            (1, 3, NOW()),
                                                            (1, 4, NOW()),
                                                            (1, 5, NOW()),
                                                            (1, 6, NOW()),
                                                            (1, 7, NOW()),

                                                            (2, 1, NOW()),
                                                            (2, 3, NOW()),
                                                            (2, 4, NOW()),
                                                            (2, 5, NOW()),
                                                            (2, 6, NOW()),
                                                            (2, 7, NOW()),
                                                            (2, 8, NOW()),
                                                            (2, 9, NOW()),
                                                            (2, 10, NOW()),
                                                            (2, 11, NOW()),

                                                            (3, 2, NOW()),
                                                            (3, 4, NOW()),
                                                            (3, 6, NOW()),
                                                            (3, 8, NOW()),

                                                            (4, 3, NOW()),
                                                            (4, 5, NOW()),
                                                            (4, 7, NOW()),
                                                            (4, 9, NOW()),

                                                            (5, 1, NOW()),
                                                            (5, 4, NOW()),
                                                            (5, 8, NOW()),
                                                            (5, 9, NOW()),
                                                            (5, 10, NOW()),
                                                            (5, 11, NOW()),
                                                            (5, 14, NOW()),

                                                            (6, 2, NOW()),
                                                            (6, 15, NOW()),
                                                            (6, 14, NOW()),
                                                            (6, 13, NOW()),
                                                            (6, 7, NOW()),
                                                            (6, 8, NOW()),
                                                            (6, 9, NOW()),
                                                            (6, 10, NOW()),
                                                            (6, 11, NOW()),
                                                            (6, 12, NOW()),

                                                            (7, 3, NOW()),
                                                            (7, 4, NOW()),
                                                            (7, 12, NOW()),

                                                            (8, 1, NOW()),
                                                            (8, 5, NOW()),
                                                            (8, 6, NOW()),
                                                            (8, 15, NOW()),
                                                            (8, 7, NOW()),
                                                            (8, 8, NOW()),
                                                            (8, 9, NOW()),
                                                            (8, 10, NOW()),
                                                            (8, 11, NOW()),

                                                            (9, 2, NOW()),
                                                            (9, 3, NOW()),
                                                            (9, 6, NOW()),
                                                            (9, 7, NOW()),
                                                            (9, 8, NOW()),
                                                            (9, 10, NOW()),
                                                            (9, 11, NOW()),
                                                            (9, 12, NOW()),

                                                            (10, 4, NOW()),
                                                            (10, 5, NOW()),
                                                            (10, 6, NOW()),
                                                            (10, 7, NOW()),
                                                            (10, 8, NOW()),
                                                            (10, 9, NOW()),
                                                            (10, 10, NOW()),
                                                            (10, 11, NOW()),

                                                            (11, 1, NOW()),
                                                            (11, 3, NOW()),
                                                            (11, 6, NOW()),
                                                            (11, 14, NOW()),
                                                            (11, 8, NOW()),
                                                            (11, 10, NOW()),
                                                            (11, 12, NOW()),

                                                            (12, 2, NOW()),
                                                            (12, 4, NOW()),
                                                            (12, 6, NOW()),
                                                            (12, 7, NOW()),
                                                            (12, 8, NOW()),
                                                            (12, 9, NOW()),
                                                            (12, 11, NOW());

-- =========================
-- 3) INDEXES
-- =========================

CREATE INDEX IF NOT EXISTS idx_videos_geohash ON videos (geohash);
CREATE INDEX IF NOT EXISTS idx_videos_geohash_prefix ON videos (geohash varchar_pattern_ops);

-- =========================
-- 4) SEQUENCE FIX (OPTIONAL, SAFE)
-- =========================

SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users), true);
SELECT setval(pg_get_serial_sequence('videos', 'id'), (SELECT COALESCE(MAX(id), 1) FROM videos), true);
SELECT setval(pg_get_serial_sequence('comments', 'id'), (SELECT COALESCE(MAX(id), 1) FROM comments), true);
SELECT setval(pg_get_serial_sequence('video_likes', 'id'), (SELECT COALESCE(MAX(id), 1) FROM video_likes), true);

COMMIT;
