# Automatic analysis and simplification of architectural floor plans [![DOI](https://zenodo.org/badge/69436030.svg)](https://zenodo.org/badge/latestdoi/69436030)
This software is an architectural floor plan analysis and recognition system to create extended plans for building services.

## Abstract
The goal of this work is to do a fast and robust room detection on floor plans. The idea is, that a wide range of non standardized floor plans can be analyzed, time efficient, with little drawbacks in its precision.
The used workflow consists of several algorithms, that are combined to deliver the expected result. It consists of *Morphological cleaning* for noise removal, *Machine Learning* and *Convex Hull closing* for gap closing and a *Connected Component analysis* for the final room detection. It is the best result out of different approaches that were tested. All of the algorithms used, use an image of a plan  as the start for detection and return the location and size of each room as a CSV-table or SVG-vectors. The software is prepared to return the rooms as a DWG- or DXF-Format for a CAD-Program, but the license for a library, to convert the format, is not finally evaluated. The algorithm implemented, shows improvement in room detection accuracy, compared to similar works done in the last few years.

![Afpars](readme/afpars.jpg)

## Run the application
The software is still a prototype and not packaged into an executable. To run the software, you have to run the following command:

```bash
cd afpars

# unix / macOS
./gradlew run

# windows
gradlew.bat run
```

## Development prerequisites
For development on the existing project, you have to install a modern Java Development Kit (`>=11`). OpenJDK is supported!

The project itself can be built with the [gradle build tool](https://gradle.org/).

```bash
cd afpars

# unix / macOS
./gradlew build

# windows
gradlew.bat build
```

## About
*FHNW Bachelor Computer Science*

*Alexander Wyss and Florian Bruggisser*
