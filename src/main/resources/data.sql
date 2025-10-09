INSERT INTO devices (name,type, status,room) VALUES
('Главный свет', 'LIGHT', 'OFF', 'Гостиная'),
('Кондиционер', 'AIR_CONDITIONER', 'OFF', 'Спальня'),
('Умный термостат', 'THERMOSTAT', 'ON', 'Гостиная'),
('Телевизор', 'TV', 'OFF', 'Гостиная');

INSERT INTO sensors (name, type, value, room) VALUES 
('Датчик температуры', 'TEMPERATURE', 22.5, 'Гостиная'),
('Датчик движения', 'MOTION', 0, 'Коридор'),
('Датчик освещенности', 'LUMINOSITY', 85.0, 'Гостиная'),
('Датчик влажности', 'HUMIDITY', 45.0, 'Спальня');