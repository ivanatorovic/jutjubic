

INSERT INTO users (email,username,password,first_name,last_name,address,enabled,activation_token,created_at)VALUES
    ('pera@gmail.com','pera','$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6','Pera','Peric','Bulevar Oslobodjenja 22',true,NULL, NOW()),
    ('zika@gmail.com','zika','$2a$12$NHk/iCm/q0uILyn1MxnKJOpHMwtqpDaMofDsjMrBjFpef1ZN0h/S6','Zika','Zikic','Bulevar Evrope 2',true,NULL, NOW());




INSERT INTO videos (title, description, video_path, thumbnail_path, user_id, created_at,view_count)
VALUES
    ('Primer 1', 'Test video',
     'storage\\videos\\14247790-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\klavir.jpg',
     1,
     NOW() - INTERVAL '5 days',0),

    ('Primer 2', 'Još jedan video',
     'storage\\videos\\14346684_2160_3840_30fps.mp4',
     'storage\\thumbnails\\gradska_plaza2.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours',0),

    ('Primer 3', 'Još jedan video',
     'storage\\videos\\293085_small.mp4',
     'storage\\thumbnails\\trening.jpg',
     2,
     NOW() - INTERVAL '3 days',0),

    ('Primer 4', 'Još jedan video',
     'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\tiganj.jpg',
     2,
     NOW() - INTERVAL '2 days 6 hours',0),

    ('Primer 5', 'Još jedan video',
     'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4',
     'storage\\thumbnails\\potcast.jpg',
     1,
     NOW() - INTERVAL '1 day',0),

    ('Primer 6', 'Još jedan video',
     'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4',
     'storage\\thumbnails\\bicikli.jpg',
     1,
     NOW() - INTERVAL '20 hours',0),

    ('Primer 7', 'Još jedan video',
     'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\sminka.jpg',
     2,
     NOW() - INTERVAL '8 hours',0),

    ('Primer 8', 'Još jedan video',
     'storage\\videos\\19540795-uhd_2160_3840_25fps.mp4',
     'storage\\thumbnails\\4520325175bb3a5390da09564646869_w640.jpg',
     2,
     NOW() - INTERVAL '1 hour',0);

-- komentari (pera=1, zika=2; video id pretpostavimo 1..)
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



-- lajkovi
INSERT INTO video_likes (video_id, user_id,created_at)
VALUES
    (1, 1,NOW()),
    (1, 2,NOW()),
    (2, 2,NOW());


