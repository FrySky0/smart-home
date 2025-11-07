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
INSERT INTO devices (name, type, status, room_id, value) VALUES 
('Основной свет', 'LIGHT', 'ON', 1, NULL),
('Термостат', 'THERMOSTAT', 'ON', 1, NULL),
('Телевизор', 'LIGHT', 'OFF', 1, NULL),
('Кухонный свет', 'LIGHT', 'ON', 2, NULL),
('Холодильник', 'AIR_CONDITIONER', 'ON', 2, NULL),
('Свет в спальне', 'LIGHT', 'OFF', 3, NULL),
('Кондиционер', 'AIR_CONDITIONER', 'ON', 3, 12.3);

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

INSERT INTO automation_rules (name, description, trigger_device_id, trigger_sensor_id, enabled, trigger_event, trigger_value, action,action_value) VALUES 
('Отключить кондиционер в спальне', 'Отключает кондиционер в спальне при превышении температуры', 7, 6, true, 'GREATER_THAN', 20, 'SET_VALUE', 10.0),
('Включить свет в гостиной при движении', 'Включает свет в гостиной при обнаружении движения', 1, 2, true, 'EQUALS', 1, 'TURN_ON', NULL),
('Выключить кухонный свет при низкой освещенности', 'Выключает кухонный свет при низкой освещенности', 4, 3, true, 'LESS_THAN', 300, 'TURN_OFF', NULL);