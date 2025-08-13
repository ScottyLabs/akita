#!/bin/bash
export VAULT_ADDR=https://secrets.scottylabs.org

# Login to vault
# vault login -method=oidc

# Usage
usage() {
  echo
  echo -e "\tUsage: $0 ENVIRONMENT --push | --pull\n"
  echo -e "\t\tENVIRONMENT: The environment to operate on, one of local | dev | staging | prod | all\n"
  echo -e "\t\t--push    Push environment variables to Vault"
  echo -e "\t\t--pull    Pull environment variables from Vault\n"
  echo -e "\tOptions:"
  echo -e "\t\t-h, --help    Show this help message and exit\n"
}

# Initialize variables
OPERATION=""
ENVIRONMENT=""

# Parse arguments
while [[ "$#" -gt 0 ]]; do
  case "$1" in
  -h | --help)
    usage
    exit 0
    ;;
  --push)
    OPERATION="push"
    shift
    ;;
  --pull)
    OPERATION="pull"
    shift
    ;;
  *)
    if [ -z "$ENVIRONMENT" ]; then
      ENVIRONMENT="$1"
    else
      echo "Error: Unexpected argument: '$1'" >&2
      usage
      exit 1
    fi
    shift
    ;;
  esac
done

# Validate operation and environment
if [ -z "$OPERATION" ]; then
  echo "Error: Please specify either --push or --pull." >&2
  usage
  exit 1
fi

if [ -z "$ENVIRONMENT" ]; then
  echo "Error: Please specify an ENVIRONMENT." >&2
  usage
  exit 1
fi

# Sanitizing the Environment argument
if [ "$ENVIRONMENT" == "all" ]; then
  ENVIRONMENT=("local" "dev" "staging" "prod")
else
  case "$ENVIRONMENT" in
  "local" | "dev" | "staging" | "prod")
    ENVIRONMENT=("$ENVIRONMENT")
    ;;
  *)
    echo "Error: Invalid environment: '$ENVIRONMENT'" >&2
    usage
    exit 1
    ;;
  esac
fi

# Perform the selected operation
for ENV in "${ENVIRONMENT[@]}"; do
  ENV_FILE=".env.$ENV"
  case "$OPERATION" in
  pull)
    echo "Pulling from Vault for environment: $ENV"
    vault kv get -format=json ScottyLabs/akita/"$ENV" |
      jq -r '.data.data | to_entries[] | "\(.key)=\"\(.value)\""' >"$ENV_FILE"
    ;;
  push)
    echo "Pushing to Vault for environment: $ENV"
    cat "$ENV_FILE" | xargs -r vault kv put -mount="ScottyLabs" "akita/$ENV"
    ;;
  esac
done
