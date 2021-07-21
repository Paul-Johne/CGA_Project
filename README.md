# Projektname
>Swipe MI

# Teammitglieder
- Kevin Repke
- Paul André Johne
- Sara Thomas

# Prüfer mit Gitnamen
- Uwe Müsse (umuesse)
- Fabian Friederichs (fabianfriederichs)
- Jannis M. (jm-th-koeln)

# Featureliste
>grundlegende Idee:
- Erstellung einer Demo eines isometrischen "Low-Poly-Puzzle-Tile-Slide-Game" namens Swipe MI
  - Welt aus verschiebbaren Plattformen
  - konstanter Wechsel zwischen "Verschiebe"- & "Erkundungs"-Modus
  - Wincondition: Schlüsselobjekt von A nach B bringen
>Rendering:
- Erstellen von 3D Modellen => Tiles, Schlüsselobjekt(e), Spielfigur
>Shaderprogrammierung:
- Shaderwechsel Phong & Schwarz-Weiß
- Textureblending durch beispielsweise Glas
- Parallex Mapping
>Transformationen:
- automatische Transformation ("Rascheln" von Blättern eines z.B. Baumes, der auf einer Tile steht)
- aufgehobene Schlüsselobjekte werden mit der Figur mitbewegt (interaktive abhängige Transformation)
- Räder der Spielfigur rotieren während gesamtes Objekt sich fortbewegt (mehrstufige Transformationshierarchie)
>Kamera:
- Rotieren zu festen Positionen um die Welt
- isometrische Perspektive
- Zoomfunktion
>Texturen:
- Cubemap(s) in welcher sich die Welt befindet
- animierte Wassertexture auf Tiles, die Wasserelemente besitzen
>Weitergehende Konzepte:
- Objekt Kollision
- Shadow Mapping
- Hintergrundmusik (z.B. mit PlaySound Funktion von Mikrosoft)
