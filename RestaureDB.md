# Restaurer la DB 

## Créer un dump depuis le container

```bash
docker exec postgres-dev pg_dump -U betterdle -d betterdle -F c -b > betterdle.dump
```

## 1️ Copier le dump dans le container

```bash
PS C:\Users\rodne\Desktop\Projet Dev\Perso\BetterDLE> .
```

```bash
docker cp betterdle-api/tmp/betterdle.dump postgres-dev:/tmp/dump.dump
```

## 2️ Recréer la base

```bash
docker exec -it postgres-dev psql -U betterdle -d postgres -c "DROP DATABASE IF EXISTS betterdle;" ; docker exec -it postgres-dev psql -U betterdle -d postgres -c "CREATE DATABASE betterdle;"
```

## 3 Restaurer

```bash
docker exec -it postgres-dev pg_restore -U betterdle -d betterdle /tmp/dump.dump
```

# Exécuter du SQL

## Shell interactif

```bash
docker exec -it postgres-dev psql -U betterdle -d betterdle
```