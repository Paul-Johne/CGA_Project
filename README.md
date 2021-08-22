# Projektname
>Swipe MI

# Teammitglieder
- Kevin Repke (11141317)
- Paul André Johne (11138534)
- Sara Thomas (11131860)

# Prüfer mit Gitnamen
- Uwe Müsse (umuesse)
- Fabian Friederichs (fabianfriederichs)
- Jannis M. (jm-th-koeln)

# Featureliste
>grundlegende Idee:
- Erstellung einer Demo eines isometrischen "Low-Poly-Puzzle-Tile-Slide-Game" namens Swipe MI [Sara]
  - Welt aus verschiebbaren Plattformen
  - konstanter Wechsel zwischen "Verschiebe"- & "Erkundungs"-Modus
  - Wincondition: Schlüsselobjekt von A nach B bringen
>Rendering:
- Erstellen von 3D Modellen => Tiles, Schlüsselobjekt(e), Spielfigur [Sara, Paul]
>Shaderprogrammierung:
- Shaderwechsel Standard zu Schwarz-Weiß [Kevin]
- mehrere Materialien (Wall vs. Rest) in einem Shader verarbeiten [Paul]
- Dynamisches Normal Mapping [Paul]
>Transformationen:
- automatische Transformation (Rotation des Schlüssels) [Sara]
- aufgehobene Schlüsselobjekte werden mit der Figur mitbewegt (interaktive abhängige Transformation) [Sara]
>Kamera:
- Rotieren zu festen Positionen um die Welt [Sara]
- isometrische Perspektive [Sara]
- Zoomfunktion [Sara, Paul]
>Texturen:
- Cubemap(s) in welcher sich die Welt befindet [Kevin, Paul]
- Transparenz von Wasser mittels Blending [Kevin] -> s. Video
>Weitergehende Konzepte:
- Objekt Kollision [Kevin] -> s. Video
#ANMERKUNG Dieses Feature ist im branch collision_Detection als Demo abspielbar. Wegen der Zeit und Komplikationen konnte dies in der Main nicht implementiert werden.
- Hintergrundmusik [Paul]
