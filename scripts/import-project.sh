#!/bin/bash

# Set the projects directory
PROJECTS_DIR="projects"

# Check if projects directory exists
if [ ! -d "$PROJECTS_DIR" ]; then
  echo "Error: Directory $PROJECTS_DIR does not exist."
  exit 1
fi

# List available projects
echo "Available projects:"
ls -1 "$PROJECTS_DIR"
echo ""

# Prompt for project name
read -p "Enter the project name: " PROJECT_NAME

# Validate project exists
PROJECT_PATH="$PROJECTS_DIR/$PROJECT_NAME"
if [ ! -d "$PROJECT_PATH" ]; then
  echo "Error: Project '$PROJECT_NAME' does not exist in $PROJECTS_DIR."
  exit 1
fi

# Check if models directory exists
MODELS_DIR="$PROJECT_PATH/models"
if [ ! -d "$MODELS_DIR" ]; then
  echo "Error: Models directory $MODELS_DIR does not exist."
  exit 1
fi

# Ensure scripts directory exists
mkdir -p scripts

# Output SQL file
SQL_FILE="${PROJECT_NAME}_insert.sql"

# Current timestamp for created_at and updated_at
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# Start writing SQL file
cat << EOF > "$SQL_FILE"
-- Insert into projects table
INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 1, '$PROJECT_NAME', '/$PROJECT_NAME', '$TIMESTAMP', '$TIMESTAMP', 'admin@uet.vn', 'admin@uet.vn');

-- Insert into models table
INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
EOF

# Get list of .gaml files and generate model inserts
MODEL_FILES=($(ls "$MODELS_DIR"/*.gaml 2>/dev/null))
MODEL_COUNT=${#MODEL_FILES[@]}
MODEL_ID=1

for ((i=0; i<MODEL_COUNT; i++)); do
  # Extract model name without .gaml extension
  MODEL_NAME=$(basename "${MODEL_FILES[$i]}" .gaml)
  echo "    ($MODEL_ID, '$MODEL_NAME', 1, 1, '$TIMESTAMP', '$TIMESTAMP', 'admin@uet.vn', 'admin@uet.vn')" >> "$SQL_FILE"
  # Add comma unless it's the last model
  if [ $i -lt $((MODEL_COUNT-1)) ]; then
    echo "," >> "$SQL_FILE"
  else
    echo ";" >> "$SQL_FILE"
  fi
  ((MODEL_ID++))
done

# Initialize experiments section
echo "" >> "$SQL_FILE"
echo "-- Insert into experiments table" >> "$SQL_FILE"
echo "INSERT INTO experiments" >> "$SQL_FILE"
echo "(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)" >> "$SQL_FILE"
echo "VALUES" >> "$SQL_FILE"

# Generate experiment inserts based on experiment declarations in .gaml files
EXPERIMENT_ID=1
EXPERIMENT_VALUES=()

for MODEL_FILE in "${MODEL_FILES[@]}"; do
  MODEL_NAME=$(basename "$MODEL_FILE" .gaml)
  # Look for 'experiment ExperimentName' in the file
  EXPERIMENT_NAME=$(grep -E "^experiment\s+\S+" "$MODEL_FILE" | head -1 | awk '{print $2}' | tr -d '[:space:]')
  if [ -n "$EXPERIMENT_NAME" ]; then
    # Find the model_id for this model
    MODEL_INDEX=$(grep -n "'$MODEL_NAME'" "$SQL_FILE" | cut -d'(' -f2 | cut -d',' -f1 | head -1)
    MODEL_INDEX=$((MODEL_INDEX))
    if [ -n "$MODEL_INDEX" ]; then
      EXPERIMENT_VALUES+=("    ($EXPERIMENT_ID, '$EXPERIMENT_NAME', $MODEL_INDEX, 1, 1, '$TIMESTAMP', '$TIMESTAMP', 'admin@uet.vn', 'admin@uet.vn')")
      ((EXPERIMENT_ID++))
    fi
  fi
done

# Write experiment values
if [ ${#EXPERIMENT_VALUES[@]} -gt 0 ]; then
  for ((i=0; i<${#EXPERIMENT_VALUES[@]}; i++)); do
    echo "${EXPERIMENT_VALUES[$i]}" >> "$SQL_FILE"
    if [ $i -lt $(( ${#EXPERIMENT_VALUES[@]}-1 )) ]; then
      echo "," >> "$SQL_FILE"
    else
      echo ";" >> "$SQL_FILE"
    fi
  done
else
  echo "    -- No experiments defined for this project;" >> "$SQL_FILE"
fi

echo "SQL file generated: $SQL_FILE"
