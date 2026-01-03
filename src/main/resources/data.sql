

INSERT INTO users (email,username,password,first_name,last_name,address,enabled,activation_token,created_at)VALUES
    ('pera@gmail.com','pera','$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6','Pera','Peric','Bulevar Oslobodjenja 22',true,NULL, NOW());




INSERT INTO videos (title, description, video_path, thumbnail_path, user_id, created_at)
VALUES
    ('Primer 1', 'Test video',
     'storage\\videos\\14247790-hd_1920_1080_30fps.mp4',
     'storage\\thumbnails\\klavir.jpg',
     1,
     NOW() - INTERVAL '5 days'),

    ('Primer 2', 'Još jedan video',
     'storage\\videos\\14346684_2160_3840_30fps.mp4',
     'storage\\thumbnails\\gradska_plaza2.jpg',
     1,
     NOW() - INTERVAL '4 days 3 hours'),

    ('Primer 3', 'Još jedan video',
     'storage\\videos\\293085_small.mp4',
     'storage\\thumbnails\\trening.jpg',
     2,
     NOW() - INTERVAL '3 days'),

    ('Primer 4', 'Još jedan video',
     'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\tiganj.jpg',
     2,
     NOW() - INTERVAL '2 days 6 hours'),

    ('Primer 5', 'Još jedan video',
     'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4',
     'storage\\thumbnails\\potcast.jpg',
     1,
     NOW() - INTERVAL '1 day'),

    ('Primer 6', 'Još jedan video',
     'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4',
     'storage\\thumbnails\\bicikli.jpg',
     1,
     NOW() - INTERVAL '20 hours'),

    ('Primer 7', 'Još jedan video',
     'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4',
     'storage\\thumbnails\\sminka.jpg',
     2,
     NOW() - INTERVAL '8 hours'),

    ('Primer 8', 'Još jedan video',
     'storage\\videos\\19540795-uhd_2160_3840_25fps.mp4',
     'storage\\thumbnails\\4520325175bb3a5390da09564646869_w640.jpg',
     2,
     NOW() - INTERVAL '1 hour');

-- komentari (pera=1, zika=2; video id pretpostavimo 1..)
INSERT INTO comments (text, video_id, user_id, created_at)
VALUES
    ('Top video!', 1, 1, NOW()),
    ('Brutalno!', 1, 2, NOW()),
    ('Nice', 2, 2, NOW());


-- lajkovi
INSERT INTO video_likes (video_id, user_id,created_at)
VALUES
    (1, 1,NOW()),
    (1, 2,NOW()),
    (2, 2,NOW());


