#!/bin/bash
source .env.local

usage() {
  echo
  echo -e "\tUsage: $0 SERVICE ENVIRONMENT\n"
  echo -e "\t\tENVIRONMENT: The environment to push to, one of dev | staging | prod | all\n"
  echo -e "\tOptions:"
  echo -e "\t\t-h, --help    Show this help message and exit\n"
}

# Parse arguments
while [[ "$#" -gt 0 ]]; do
  case "$1" in
  -h | --help)
    usage
    exit 0
    ;;
  *)
    ENVIRONMENT="$1"
    ;;
  esac
  shift
done

# Sanitizing the Environment argument
if [ "$ENVIRONMENT" == "all" ]; then
  ENVIRONMENTS=("dev" "staging" "prod")
else
  case "$ENVIRONMENT" in
  "dev" | "staging" | "prod")
    ENVIRONMENTS=("$ENVIRONMENT")
    ;;
  *)
    echo "Error: Invalid environment: '$ENVIRONMENT'" >&2
    usage
    exit 1
    ;;
  esac
fi

# Pushing to railway
for ENVIRONMENT in "${ENVIRONMENTS[@]}"; do
  railway link -p $RAILWAY_PROJECT_ID -e $ENVIRONMENT -s akita
  FILENAME=".env.$ENVIRONMENT"
  while IFS='=' read -r key value || [ -n "$key" ]; do
    RAILWAY_SET_ARGS+=" --set $key=${value//\"/}"
  done <"$FILENAME"
  railway variables$RAILWAY_SET_ARGS
done
