CREATE TABLE game_logs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id VARCHAR(100),
    from_position INT,
    to_position INT,
    move_time DOUBLE,
    moved_by ENUM('USER', 'BOT') NOT NULL,
    action ENUM('MOVE', 'JUMP', 'PLACE') NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game_sessions(game_id) ON DELETE CASCADE ON UPDATE CASCADE
);