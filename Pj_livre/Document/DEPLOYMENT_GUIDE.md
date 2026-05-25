# Guide de Déploiement - Application Gestion de Livre Scolaire

## 📋 Prérequis

Avant de déployer l'application, assurez-vous que les éléments suivants sont installés :

- **Java Development Kit (JDK)** : version 8 ou supérieure
- **Apache Ant** : version 1.10 ou supérieure (pour la compilation)
- **Git** : pour cloner le repository

Vérifiez les installations :
```bash
java -version
javac -version
ant -version
```

---

## 🔧 Compilation et Build

### Sous Linux/macOS

```bash
# Naviguer vers le répertoire du projet
cd Pj_livre

# Compiler le projet avec Ant
ant clean build

# Créer le fichier JAR
ant jar

# Le fichier JAR sera généré dans : dist/Pj_livre.jar
```

### Sous Windows

```cmd
# Naviguer vers le répertoire du projet
cd Pj_livre

# Compiler le projet avec Ant
ant clean build

# Créer le fichier JAR
ant jar

# Le fichier JAR sera généré dans : dist\Pj_livre.jar
```

---

## 🚀 Déploiement et Exécution

### Sous Linux/macOS

**Exécution directe du JAR :**
```bash
# Depuis le répertoire Pj_livre
java -jar dist/Pj_livre.jar

# Avec paramètres JVM (si nécessaire)
java -Xmx512m -Xms256m -jar dist/Pj_livre.jar
```

**Créer un script de démarrage :**
```bash
# Créer le fichier run.sh
cat > run.sh << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar dist/Pj_livre.jar
EOF

# Rendre le script exécutable
chmod +x run.sh

# Exécuter l'application
./run.sh
```

### Sous Windows

**Exécution directe du JAR :**
```cmd
# Depuis le répertoire Pj_livre
java -jar dist\Pj_livre.jar

# Avec paramètres JVM (si nécessaire)
java -Xmx512m -Xms256m -jar dist\Pj_livre.jar
```

**Créer un script de démarrage (fichier batch) :**
```batch
# Créer le fichier run.bat
@echo off
cd /d "%~dp0"
java -jar dist\Pj_livre.jar
pause
```

Vous pouvez ensuite double-cliquer sur `run.bat` pour exécuter l'application.

---

## 📦 Distribution et Installation

### Empaquetage complet pour distribution

**Sous Linux/macOS :**
```bash
# Créer une archive avec l'application compilée
cd Pj_livre
ant clean
ant jar
cd ..

# Créer une archive tar.gz
tar -czf Pj_livre-dist.tar.gz Pj_livre/dist Pj_livre/Document

# Pour déployer, extraire l'archive sur la machine cible
tar -xzf Pj_livre-dist.tar.gz
```

**Sous Windows :**
```cmd
# Via PowerShell
cd Pj_livre
ant clean
ant jar
cd ..

# Créer un fichier ZIP
powershell -Command "Compress-Archive -Path Pj_livre\dist, Pj_livre\Document -DestinationPath Pj_livre-dist.zip"

# Pour déployer, extraire le ZIP sur la machine cible
```

---

## 🗄️ Configuration de la Base de Données

Si votre application utilise une base de données (selon le MCD présent dans le dossier Document) :

### Initialisation de la base de données

**Sous Linux/macOS :**
```bash
# Exemple avec MySQL/MariaDB
mysql -u root -p < Pj_livre/Document/database_setup.sql

# Ou avec PostgreSQL
psql -U postgres -f Pj_livre/Document/database_setup.sql
```

**Sous Windows (Command Prompt) :**
```cmd
# Exemple avec MySQL
mysql -u root -p < Pj_livre\Document\database_setup.sql

# Via PowerShell
Get-Content Pj_livre\Document\database_setup.sql | mysql -u root -p
```

---

## 🔐 Paramètres de Configuration

Éditez les fichiers de configuration si nécessaire avant le déploiement :

- **Fichier de propriétés** : `Pj_livre/nbproject/project.properties`
- **Paramètres JVM** : Modifiez les valeurs `-Xmx` et `-Xms` selon votre disponibilité mémoire

---

## ✅ Vérification du Déploiement

### Sous Linux/macOS

```bash
# Vérifier que l'application démarre sans erreur
java -jar Pj_livre/dist/Pj_livre.jar &

# Afficher les logs
tail -f application.log

# Arrêter l'application
killall java
```

### Sous Windows

```cmd
# Vérifier que l'application démarre
java -jar Pj_livre\dist\Pj_livre.jar

# Arrêter l'application (Ctrl+C)
```

---

## 📝 Dépannage

| Problème | Solution |\n| --- | --- |\n| `ant: command not found` | Installer Apache Ant ou ajouter son chemin aux variables d'environnement PATH |\n| `java: command not found` | Installer le JDK ou ajouter son chemin aux variables d'environnement PATH |\n| `ClassNotFoundException` | Vérifier que le manifest.mf contient la classe principale correcte |\n| Problèmes de mémoire | Augmenter les paramètres `-Xmx` lors de l'exécution |\n| Problèmes de base de données | Vérifier la connexion et les identifiants de connexion |\n\n---\n\n## 📞 Support et Maintenance\n\nPour toute question ou problème de déploiement :\n- Consultez la documentation du projet\n- Vérifiez les logs d'erreur\n- Contactez l'équipe de développement\n\n---\n\n**Version du guide** : 1.0  \n**Dernière mise à jour** : 2026-05-25"
