-- Tabela de usuários
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- Tabela de análises
CREATE TABLE analysis (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    job_title VARCHAR(200) NOT NULL,
    analysis_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    overall_score DOUBLE PRECISION,
    ai_recommendation TEXT,
    CONSTRAINT fk_analysis_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para buscar análises por usuário
CREATE INDEX idx_analysis_user_id ON analysis(user_id);
CREATE INDEX idx_analysis_date ON analysis(analysis_date DESC);

-- Tabela de tarefas
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    human_core_score DOUBLE PRECISION,
    classification VARCHAR(20) NOT NULL,
    reason TEXT,
    CONSTRAINT fk_task_analysis FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE
);

-- Índice para buscar tarefas por análise
CREATE INDEX idx_task_analysis_id ON task(analysis_id);
CREATE INDEX idx_task_classification ON task(classification);

-- Tabela de recomendações
CREATE TABLE recommendation (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    up_skill VARCHAR(150) NOT NULL,
    course_suggestion VARCHAR(200) NOT NULL,
    impact_level VARCHAR(20) NOT NULL,
    CONSTRAINT fk_recommendation_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
);

-- Índice para buscar recomendações por tarefa
CREATE INDEX idx_recommendation_task_id ON recommendation(task_id);
CREATE INDEX idx_recommendation_impact_level ON recommendation(impact_level);

-- Comentários nas tabelas
COMMENT ON TABLE users IS 'Tabela de usuários do sistema';
COMMENT ON TABLE analysis IS 'Tabela de análises de trabalho realizadas';
COMMENT ON TABLE task IS 'Tabela de tarefas identificadas nas análises';
COMMENT ON TABLE recommendation IS 'Tabela de recomendações de upskilling';

-- Comentários nas colunas importantes
COMMENT ON COLUMN analysis.overall_score IS 'Score geral de 0 a 100 indicando potencial humano';
COMMENT ON COLUMN task.human_core_score IS 'Score de 0 a 100 de quanto a tarefa requer capacidades humanas';
COMMENT ON COLUMN task.classification IS 'Classificação: HUMAN, HYBRID ou AUTOMATED';
COMMENT ON COLUMN recommendation.impact_level IS 'Nível de impacto: HIGH, MEDIUM ou LOW';
