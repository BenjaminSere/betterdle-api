#!/bin/bash
set -e

DUMP_FILE="/docker-entrypoint-initdb.d/betterdle.dump"

if [ -f "$DUMP_FILE" ]; then
    echo "Found dump file at $DUMP_FILE. Restoring..."
    pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" -1 "$DUMP_FILE" || echo "Restore might have warnings, but continuing..."
    echo "Restore complete."
else
    echo "No dump file found at $DUMP_FILE. Skipping restore."
fi
