# Java_livre

Présentation du projet

Cette application a pour objectif de gérer le cycle de vie des manuels scolaires mis à disposition des apprenants au sein d’un campus. Elle permet l’achat des manuels, leur attribution aux étudiants, le suivi des emprunts, le rachat et le retour des livres en fin d’année, ainsi qu’un suivi financier détaillé.

L’application vise à offrir un suivi standardisé sur plusieurs années et une meilleure lisibilité financière pour l’ensemble des utilisateurs.

Architecture technique

L’application est une application lourde reposant sur une architecture MVC (Model – View – Controller).
Les données sont sur une base de données MySQL hébergée sur un serveur.
La modélisation des données s’appuie sur un MCD fourni dans le sous-dossier du projet \Documents\mcd_bourse_livres.pdf .
Le respect du CRUD (Create, Read, Update, Delete) est obligatoire pour l’ensemble des entités du système.

Gestion des rôles et des droits

L’application implémente une gestion des utilisateurs par rôles, avec des droits strictement définis.

L’apprenant peut uniquement consulter les livres qu’il a empruntés ainsi que les informations associées (état du paiement, méthode de paiement).

Le formateur et l’enseignant peuvent consulter les livres empruntés par un étudiant, sans possibilité de modification.

Le gestionnaire dispose d’une vue globale sur les livres empruntés. Il peut attribuer des manuels, valider les retours, suivre les paiements, vérifier les montants et les méthodes de règlement. Il peut agir sur l’ensemble du système lié aux livres et aux finances, à l’exception de la gestion des utilisateurs et des rôles.

L’administrateur dispose d’un accès complet à l’application. Il peut gérer les utilisateurs, créer, modifier et supprimer les rôles, et superviser l’ensemble des fonctionnalités du système.

Gestion des manuels scolaires

Chaque manuel est suivi de manière individuelle. Le système conserve l’historique des emprunts, l’identité de l’étudiant concerné, les informations liées à la vente ou au rachat, la méthode de paiement choisie, le statut du règlement ainsi que l’état du livre (disponible, emprunté, retourné).

Suivi financier

Le module de suivi financier permet de connaître précisément l’origine des paiements, leur mode de règlement et leur statut. Il garantit une traçabilité complète des transactions sur plusieurs années.

Base de données

La base de données MySQL est conçue à partir du MCD fourni. Elle assure la cohérence des données, la gestion des relations entre utilisateurs, rôles, livres et paiements, ainsi que l’évolutivité du système.
** La version compilé est dans le-sous dossier version et les documents importants sont dans le sous-dossier Documents
