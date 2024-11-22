package BasicJava.ImageConverter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Paths;

public class ImageConverter {
    
    public enum ConversionType {
        GREY_SCALE {
            @Override
            public int applyTransformation(Color color) {
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                int grayValue = (red + green + blue) / 3;
                return new Color(grayValue, grayValue, grayValue).getRGB();
            }
            @Override
            public int applyTransformation(Color color, double gamma) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color, int levels) {
                return color.getRGB();
            }
        },
        INVERSION {
            @Override
            public int applyTransformation(Color color) {
                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();
                return new Color(red, green, blue).getRGB();
            }
            @Override
            public int applyTransformation(Color color, double gamma) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color, int levels) {
                return color.getRGB();
            }
        },
        SEPIA_TONE {
            @Override
            public int applyTransformation(Color color) {
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int sepiaRed = Math.min(255, (int)(0.393 * red + 0.769 * green + 0.189 * blue));
                int sepiaGreen = Math.min(255, (int)(0.349 * red + 0.686 * green + 0.168 * blue));
                int sepiaBlue = Math.min(255, (int)(0.272 * red + 0.534 * green + 0.131 * blue));

                return new Color(sepiaRed, sepiaGreen, sepiaBlue).getRGB();
            }
            @Override
            public int applyTransformation(Color color, double gamma) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color, int levels) {
                return color.getRGB();
            }
        },
        BRIGHTNESS_ADJUSTMENT {
            @Override
            public int applyTransformation(Color color) {
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int BARed = Math.min(255, red + blue);
                int BAGreen = Math.min(255, green + blue);
                int BABlue = Math.min(255, blue + blue);

                return new Color(BARed, BAGreen, BABlue).getRGB();
            }
            @Override
            public int applyTransformation(Color color, double gamma) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color, int levels) {
                return color.getRGB();
            }
        },
        GAMMA_CORRECTION {
            @Override
            public int applyTransformation(Color color, double gamma) {
                int red = (int)(255 * Math.pow(color.getRed() / 255.0, gamma));
                int green = (int)(255 * Math.pow(color.getGreen() / 255.0, gamma));
                int blue = (int)(255 * Math.pow(color.getBlue() / 255.0, gamma));

                // Clamping values between 0 and 255
                int GCRed = Math.min(255, Math.max(0, red));
                int GCGreen = Math.min(255, Math.max(0, green));
                int GCBlue = Math.min(255, Math.max(0, blue));

                return new Color(GCRed, GCGreen, GCBlue).getRGB();
            }
            @Override
            public int applyTransformation(Color color) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color, int levels) {
                return color.getRGB();
            }
        },
        POSTERIZATION {
            @Override
            public int applyTransformation(Color color, int levels) {
                // Apply posterization for each color channel
                int red = (color.getRed() / levels) * levels;
                int green = (color.getGreen() / levels) * levels;
                int blue = (color.getBlue() / levels) * levels;

                // Ensure values are within RGB range [0, 255]
                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                return new Color(red, green, blue).getRGB();
            }
            @Override
            public int applyTransformation(Color color, double gamma) {
                return color.getRGB();
            }

            @Override
            public int applyTransformation(Color color) {
                return color.getRGB();
            }
        };

        public abstract int applyTransformation(Color color);
        public abstract int applyTransformation(Color color, double gamma);
        public abstract int applyTransformation(Color color, int levels);
    }

    public static void convertImage(String inputFilePath, ConversionType conversionType) {
        convertImage(inputFilePath, conversionType, 0, 0);  // Use default values for gamma and levels
    }

    public static void convertImage(String inputFilePath, ConversionType conversionType, double gamma, int levels){
        String updatedFilePath = "D:/CodingSpace/JavaPractice/BasicJava/ImageConverter/assets/" + inputFilePath;
        String outputFilePath = "D:/CodingSpace/JavaPractice/BasicJava/ImageConverter/assets/converted/";

        File inputFile = new File(updatedFilePath);
        File outputDirectory = new File(outputFilePath);

        if (!inputFile.canRead()) {
            System.err.println("File not found: " + inputFile.getAbsolutePath());
        }

        // create if does not exist
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try {
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                throw new IOException("Invalid image file.");
            }

            BufferedImage processedImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
            );

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    Color color = new Color(pixel);

                    int transformedPixel;
                    if (conversionType == ConversionType.GAMMA_CORRECTION) {
                        transformedPixel = conversionType.applyTransformation(color, gamma);
                    } else if (conversionType == ConversionType.POSTERIZATION) {
                        transformedPixel = conversionType.applyTransformation(color, levels);
                    } else {
                        transformedPixel = conversionType.applyTransformation(color);
                    }

                    processedImage.setRGB(x, y, transformedPixel);
                }
            }

            String outputFileName = conversionType.name().toLowerCase() + "-" + inputFilePath;
            File outputFile = new File(outputFilePath + outputFileName);

            ImageIO.write(processedImage, "jpg", outputFile);
            System.out.println(conversionType.name() + " image created successfully: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error processing the image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputedFile = "cat.jpg";
        convertImage(inputedFile, ConversionType.GREY_SCALE);
        convertImage(inputedFile, ConversionType.INVERSION);
        convertImage(inputedFile, ConversionType.SEPIA_TONE);
        convertImage(inputedFile, ConversionType.BRIGHTNESS_ADJUSTMENT);
        convertImage(inputedFile, ConversionType.GAMMA_CORRECTION, 2.2, 0);
        convertImage(inputedFile, ConversionType.POSTERIZATION, 0, 20);
    }
}
