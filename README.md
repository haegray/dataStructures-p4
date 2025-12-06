# Blob Detection

A Java application that detects and identifies "blobs" (connected regions of similar color) in images using the Union-Find (Disjoint Sets) data structure.

## Overview

This project implements blob detection through image segmentation. Given an input image and a target color, the program:

1. Thresholds the image based on color distance from the target
2. Groups connected pixels into blobs using disjoint sets
3. Identifies and highlights the k-largest blobs
4. Outputs the processed image with detected blobs colored in a gradient

## Project Structure

| File | Description |
|------|-------------|
| `BlobDetection.java` | Main entry point - handles CLI arguments and orchestrates detection |
| `Detector.java` | Core detection logic - thresholding, pixel grouping, and output |
| `DisjointSets.java` | Union-Find implementation with union by size and path compression |
| `Set.java` | Custom linked-list based set implementation for storing pixel data |

## Usage

```bash
java BlobDetection <image_file> -k <K> -r <red> -g <green> -b <blue> -d <distance> [-o <output_file>] [-show]
```

### Parameters

| Parameter | Description |
|-----------|-------------|
| `image_file` | Input image (*.jpg, *.png, etc.) |
| `-k K` | Number of blobs to detect |
| `-r`, `-g`, `-b` | Target color RGB values (0-255) |
| `-d distance` | Acceptable color distance threshold |
| `-o output_file` | (Optional) Output file name |
| `-show` | (Optional) Display result in a window |

### Example

Detect the 3 largest red blobs in an image:

```bash
java BlobDetection input.png -k 3 -r 255 -g 0 -b 0 -d 10
```

## Output

The program generates two output images:
- `<name>_blob.png` - K largest blobs colored in gradient shades
- `<name>_blob_ec.png` - Original image with bounding box around the largest blob

## Algorithm

1. **Thresholding**: Pixels within the color distance threshold are marked black, others white
2. **Union-Find**: Adjacent black pixels are merged into sets using union by size
3. **Ranking**: Blob sets are sorted by size
4. **Coloring**: Top K blobs are recolored with gradient shades of the target color

## Requirements

- Java 8 or higher
- Java Swing (for `-show` option)
