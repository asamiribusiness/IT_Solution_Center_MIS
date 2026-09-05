
package com.itsolutioncenter.util;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public class ImageUtils {
   
    // Project folder structure
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final String IMAGE_FOLDER = PROJECT_ROOT + File.separator + "student_images";
   
    // Create image folder if it doesn't exist
    static {
        File folder = new File(IMAGE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Created image folder: " + IMAGE_FOLDER);
        }
    }
   
    /**
     * Resize image to fit in the label while maintaining aspect ratio
     */
    public static ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
       
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    public static ImageIcon loadOrDefault(String imagePath) {
    return (imagePath != null && !imagePath.isEmpty())
            ? loadImage(imagePath)
            : getDefaultImage();
}
   
    /**
     * Resize image to specific dimensions
     */
    public static ImageIcon resizeImage(Image image, int width, int height) {
        if (image == null) return null;
       
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
   
    /**
     * Create circular image (for profile pictures)
     */
    public static ImageIcon createCircularImage(ImageIcon original, int diameter) {
        if (original == null) return null;
       
        BufferedImage master = new BufferedImage(
            diameter, diameter, BufferedImage.TYPE_INT_ARGB);
       
        Graphics2D g2d = master.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
       
        // Create circular clipping area
        java.awt.geom.Ellipse2D.Double circle = new java.awt.geom.Ellipse2D.Double(0, 0, diameter, diameter);
        g2d.setClip(circle);
       
        // Draw image
        Image scaled = original.getImage().getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);
        g2d.drawImage(scaled, 0, 0, null);
       
        // Draw border
        g2d.setClip(null);
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.WHITE);
        g2d.drawOval(0, 0, diameter, diameter);
       
        g2d.dispose();
       
        return new ImageIcon(master);
    }
   
    /**
     * Save image to project folder
     * Returns the saved file path
     */
    public static String saveImageToFolder(File imageFile, String studentId) {
        try {
            // Generate unique filename
            String extension = getFileExtension(imageFile.getName());
            String newFileName = studentId + "_" + System.currentTimeMillis() + "." + extension;
            String destinationPath = IMAGE_FOLDER + File.separator + newFileName;
           
            // Copy file to project folder
            Files.copy(imageFile.toPath(), new File(destinationPath).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
           
            return "student_images/" + newFileName; // Return relative path
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
   
    /**
     * Load image from file path
     */
    public static ImageIcon loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return getDefaultImage();
        }
       
        try {
            File file;
            if (imagePath.startsWith("student_images/")) {
                // Relative path from project folder
                file = new File(PROJECT_ROOT + File.separator + imagePath);
            } else {
                // Absolute path
                file = new File(imagePath);
            }
           
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            } else {
                return getDefaultImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultImage();
        }
    }
   
    /**
     * Get default image when no image is available
     */
    public static ImageIcon getDefaultImage() {
        // Create a default profile image
        int size = 150;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
       
        // Fill background
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, size, size);
       
        // Draw a person icon
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(180, 180, 180));
       
        // Head
        g2d.fillOval(size/4, size/6, size/2, size/2);
       
        // Body
        g2d.fillRect(size/3, size/2, size/3, size/3);
       
        g2d.dispose();
       
        return new ImageIcon(image);
    }
   
    /**
     * Get file extension
     */
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "jpg"; // Default extension
    }
   
    /**
     * Delete image file
     */
    public static boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return false;
       
        try {
            File file;
            if (imagePath.startsWith("student_images/")) {
                file = new File(PROJECT_ROOT + File.separator + imagePath);
            } else {
                file = new File(imagePath);
            }
           
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
   
    /**
     * Get image folder path
     */
    public static String getImageFolderPath() {
        return IMAGE_FOLDER;
    }
}

