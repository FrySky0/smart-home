-- Очистка таблиц (для тестирования)
DELETE FROM sensors;
DELETE FROM devices;
DELETE FROM rooms;

-- Комнаты
INSERT INTO rooms (name, floor) VALUES 
('Гостиная', 1),
('Кухня', 1),
('Спальня', 2),
('Прихожая', 1);

-- Устройства
INSERT INTO devices (name, type, status, room_id) VALUES 
('Основной свет', 'LIGHT', 'ON', 1),
('Термостат', 'THERMOSTAT', 'ON', 1),
('Телевизор', 'LIGHT', 'OFF', 1),
('Кухонный свет', 'LIGHT', 'ON', 2),
('Холодильник', 'AIR_CONDITIONER', 'ON', 2),
('Свет в спальне', 'LIGHT', 'OFF', 3),
('Кондиционер', 'AIR_CONDITIONER', 'ON', 3);

-- Датчики
INSERT INTO sensors (name, type, value, room_id) VALUES 
('Температура в гостиной', 'TEMPERATURE', 22.5, 1),
('Датчик движения', 'MOTION', 1.0, 1),
('Освещенность', 'LUMINOSITY', 350.0, 1),
('Температура на кухне', 'TEMPERATURE', 24.0, 2),
('Влажность', 'HUMIDITY', 45.0, 2),
('Температура в спальне', 'TEMPERATURE', 21.0, 3),
('Датчик движения в спальне', 'MOTION', 0.0, 3),
('Датчик у входной двери', 'MOTION', 0.0, 4);