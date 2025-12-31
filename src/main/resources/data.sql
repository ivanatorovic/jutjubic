

INSERT INTO users (email,username,password,first_name,last_name,address,enabled,activation_token,created_at)VALUES
    ('pera@gmail.com','pera','$2a$10$6Fi/kOMfMZ2Rki/yxM.TDeZtEDh/.YKvUy1tYiHd69/I2rtZGGON6','Pera','Peric','Bulevar Oslobodjenja 22',true,NULL, NOW()),
    ('zika@gmail.com','zika','$2a$10$F0WwOocsX7b6Lu.myQ4gNupxGyrj.G.qbqKhaZe1Cr5fgMEEA4kdW','Zika','Zikic','Njegoseva 1',true,NULL, NOW());



INSERT INTO videos (title, description, video_path, thumbnail_path)
VALUES
    ('Primer 1', 'Test video', 'storage\\videos\\14247790-hd_1920_1080_30fps.mp4', 'storage\\thumbnails\\klavir.jpg'),
    ('Primer 2', 'Još jedan video', 'storage\\videos\\14346684_2160_3840_30fps.mp4', 'storage\\thumbnails\\gradska_plaza2.jpg'),
    ('Primer 3', 'Još jedan video', 'storage\\videos\\293085_small.mp4', 'storage\\thumbnails\\trening.jpg'),
    ('Primer 4', 'Još jedan video', 'storage\\videos\\2620043-uhd_3840_2160_25fps.mp4', 'storage\\thumbnails\\tiganj.jpg'),
    ('Primer 5', 'Još jedan video', 'storage\\videos\\coverr-a-man-happily-talks-on-a-virtual-meeting-9706-1080p.mp4', 'storage\\thumbnails\\potcast.jpg'),
    ('Primer 6', 'Još jedan video', 'storage\\videos\\3683308-uhd_3840_2160_24fps.mp4', 'storage\\thumbnails\\bicikli.jpg'),
    ('Primer 7', 'Još jedan video', 'storage\\videos\\3181592-uhd_3840_2160_25fps.mp4', 'storage\\thumbnails\\sminka.jpg'),
    ('Primer 8', 'Još jedan video', 'storage\\videos\\19540795-uhd_2160_3840_25fps.mp4', 'storage\\thumbnails\\4520325175bb3a5390da09564646869_w640.jpg');
