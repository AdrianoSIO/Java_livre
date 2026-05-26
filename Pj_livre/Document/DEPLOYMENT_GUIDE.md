# Guide de Déploiement - Application Gestion de Livre Scolaire

## 📋 Prérequis

Avant de déployer l'application, assurez-vous que les éléments suivants sont installés :

- **Java Development Kit (JDK)** : version 24 (ou supérieure - voir `project.properties`)
- **Apache Ant** : version 1.10 ou supérieure (pour la compilation)
- **MariaDB** : version 10.5+ (base de données)
- **Git** : pour cloner le repository

Vérifiez les installations :
```bash
java -version
javac -version
ant -version
mysql --version
```

---

## 🗄️ Configuration de la Base de Données

### 1. Créer la base de données

**Sous Linux/macOS et Windows :**
```bash
# Se connecter à MariaDB
mysql -u root -p

# Exécuter les commandes SQL :
CREATE DATABASE IF NOT EXISTS livre_scolaire CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'livre_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON livre_scolaire.* TO 'livre_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Initialiser les tables (si disponible)

Si un script SQL d'initialisation se trouve dans `Pj_livre/Document/` :
```bash
mysql -u livre_user -p livre_scolaire < Pj_livre/Document/init_database.sql
```

---

## 🔧 Configuration de l'Application

### Modifier les paramètres de connexion

Avant la compilation, éditer le fichier source de connexion à la base de données :

**Fichier** : `Pj_livre/src/pj_livre.BDD/CL_connexion.java`

Les paramètres à configurer :
- **Host** : `localhost` (ou adresse du serveur MariaDB)
- **Port** : `3306` (port par défaut MariaDB)
- **Base de données** : `livre_scolaire`
- **Utilisateur** : `livre_user`
- **Mot de passe** : `votre_mot_de_passe`

**Exemple de configuration** (à adapter dans le code) :
```java
String dbUrl = "jdbc:mariadb://localhost:3306/livre_scolaire";
String user = "livre_user";
String password = "votre_mot_de_passe";
```

---

## 🔨 Compilation et Build

### Sous Linux/macOS

```bash
# Naviguer vers le répertoire du projet
cd Pj_livre

# Nettoyer les fichiers compilés précédents
ant clean

# Compiler le projet avec Ant
ant build

# Créer le fichier JAR
ant jar

# Le fichier JAR sera généré dans : dist/Pj_livre.jar
```

### Sous Windows

```cmd
# Naviguer vers le répertoire du projet
cd Pj_livre

# Nettoyer les fichiers compilés précédents
ant clean

# Compiler le projet avec Ant
ant build

# Créer le fichier JAR
ant jar

# Le fichier JAR sera généré dans : dist\Pj_livre.jar
```

---

## 🚀 Exécution de l'Application

### Point d'entrée

**Classe principale** : `pj_livre.Controllers.C_livre`

### Sous Linux/macOS

**Exécution directe du JAR :**
```bash
# Depuis le répertoire Pj_livre
java -jar dist/Pj_livre.jar

# Avec paramètres JVM (augmenter la mémoire si nécessaire)
java -Xmx512m -Xms256m -jar dist/Pj_livre.jar
```

**Créer un script de démarrage :**
```bash
# Créer le fichier run.sh
cat > run.sh << 'EOF'
#!/bin/bash
# Script de démarrage - Application Gestion de Livre Scolaire
cd "$(dirname "$0")/Pj_livre"

# Vérifier que la base de données est accessible
echo "Vérification de la connexion MariaDB..."

# Démarrer l'application
echo "Démarrage de l'application..."
java -Xmx512m -Xms256m -jar dist/Pj_livre.jar

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

# Avec paramètres JVM (augmenter la mémoire si nécessaire)
java -Xmx512m -Xms256m -jar dist\Pj_livre.jar
```

**Créer un script de démarrage (fichier batch) :**
```batch
# Créer le fichier run.bat
@echo off
REM Script de démarrage - Application Gestion de Livre Scolaire
cd /d "%~dp0Pj_livre"

echo Verification de la connexion MariaDB...
echo Demarrage de l'application...

java -Xmx512m -Xms256m -jar dist\Pj_livre.jar

pause
```

Vous pouvez ensuite double-cliquer sur `run.bat` pour exécuter l'application.

---

## 📦 Distribution et Installation

### Empaquetage complet pour distribution

**Sous Linux/macOS :**
```bash
# Compiler et générer le JAR
cd Pj_livre
ant clean jar
cd ..

# Créer une archive tar.gz avec l'application compilée
tar -czf Pj_livre-dist.tar.gz Pj_livre/dist/ Pj_livre/Document/

# Pour déployer sur une autre machine
# 1. Transférer le fichier Pj_livre-dist.tar.gz
# 2. Extraire l'archive
tar -xzf Pj_livre-dist.tar.gz
# 3. Adapter la configuration de la base de données
# 4. Exécuter l'application
```

**Sous Windows :**
```cmd
# Compiler et générer le JAR
cd Pj_livre
ant clean jar
cd ..

# Créer un fichier ZIP via PowerShell
powershell -Command "Compress-Archive -Path 'Pj_livre\dist', 'Pj_livre\Document' -DestinationPath 'Pj_livre-dist.zip' -Force"

# Pour déployer sur une autre machine
# 1. Transférer le fichier Pj_livre-dist.zip
# 2. Extraire le ZIP
# 3. Adapter la configuration de la base de données
# 4. Exécuter l'application
```

---

## 🔐 Variables JVM Recommandées

Pour optimiser l'exécution sur différents environnements :

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| `-Xmx` | `512m` | Mémoire maximale allouée |
| `-Xms` | `256m` | Mémoire minimale allouée |
| `-Dfile.encoding` | `UTF-8` | Encodage des fichiers |
| `-Duser.timezone` | `UTC` | Fuseau horaire |

**Exemple complet :**
```bash
java -Xmx512m -Xms256m -Dfile.encoding=UTF-8 -Duser.timezone=UTC -jar dist/Pj_livre.jar
```

---

## ✅ Vérification du Déploiement

### Sous Linux/macOS

```bash
# Lancer l'application
java -jar Pj_livre/dist/Pj_livre.jar &

# Afficher les logs en temps réel
tail -f application.log

# Arrêter l'application
killall java

# Ou trouver le PID et l'arrêter
ps aux | grep Pj_livre
kill <PID>
```

### Sous Windows

```cmd
# Lancer l'application
java -jar Pj_livre\dist\Pj_livre.jar

# Arrêter l'application (Ctrl+C)
```

### Points de vérification

- L'application démarre sans erreur
- La connexion à MariaDB est établie
- L'interface graphique s'affiche correctement
- Les différents rôles fonctionnent (Apprenant, Formateur, Gestionnaire, Admin)

---

## 📝 Dépannage

| Problème | Cause | Solution |
|----------|-------|----------|
| `ant: command not found` | Apache Ant non installé | Installer Apache Ant ou ajouter son chemin aux variables PATH |
| `javac: command not found` | JDK non installé | Installer le JDK 24+ ou ajouter son chemin aux variables PATH |
| `ClassNotFoundException: pj_livre.Controllers.C_livre` | Classe principale non trouvée | Vérifier que la compilation s'est bien déroulée (fichier JAR corrompu) |
| `Connection refused (MariaDB)` | MariaDB non accessible | Vérifier que MariaDB est démarré et accessible sur localhost:3306 |
| `Access denied for user 'livre_user'` | Identifiants incorrects | Vérifier le nom d'utilisateur et le mot de passe MariaDB |
| `Database 'livre_scolaire' not found` | Base de données n'existe pas | Exécuter les commandes SQL d'initialisation |
| Erreur de mémoire insuffisante | Mémoire allouée insuffisante | Augmenter la valeur de `-Xmx` (ex: `-Xmx1024m`) |
| Problèmes d'encodage des caractères | Encodage UTF-8 non configuré | Utiliser `-Dfile.encoding=UTF-8` lors de l'exécution |

---

## 🏗️ Architecture de l'Application

- **Type** : Application lourde (Desktop)
- **Architecture** : MVC (Model-View-Controller)
- **Base de données** : MariaDB
- **Classe principale** : `pj_livre.Controllers.C_livre`
- **Dépendances** : 
  - MariaDB JDBC Driver (`mariadb-java-client-3.5.6.jar`)
  - JCalendar (`jcalendar-1.4.jar`)
  - Spring Framework 5.0.0

---

## 👥 Gestion des Droits et Rôles

L'application implémente 4 rôles :

1. **Apprenant** : Consultation des livres empruntés et infos de paiement
2. **Formateur/Enseignant** : Consultation des livres par étudiant (lecture seule)
3. **Gestionnaire** : Gestion complète des emprunts, retours, paiements
4. **Administrateur** : Accès complet, gestion des utilisateurs et rôles

---

## 📞 Support et Maintenance

Pour toute question ou problème de déploiement :
- Consultez le fichier `Pj_livre/Document/Contexte.txt` pour le contexte du projet
- Consultez le MCD dans `Pj_livre/Document/mcd_bourse_livres.pdf` pour la structure de la base de données
- Vérifiez les logs d'erreur lors du démarrage
- Contactez l'équipe de développement

---

**Version du guide** : 2.0  
**Dernière mise à jour** : 2026-05-25  
**Basé sur** : Configuration du projet - JDK 24, MariaDB 10.5+, Ant 1.10+
