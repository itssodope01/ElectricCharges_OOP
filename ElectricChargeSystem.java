import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ElectricChargeSystem {
    private int numberOfCharges;
    private List<Charge> charges;

    public ElectricChargeSystem(int N) {
        numberOfCharges = N;
        charges = new ArrayList<>(N);
    }

    public void specifyChargeParameters(int chargeIndex, double x, double y, double z, double chargeValue) {
        if (chargeIndex < 0 || chargeIndex >= numberOfCharges) {
            System.out.println("Invalid charge index.");
            return;
        }
        Charge charge = new Charge(x, y, z, chargeValue);
        charges.add(chargeIndex, charge);
    }

    public void printChargesAndTotalCharge() {
        double totalCharge = 0.0;
        for (int i = 0; i < numberOfCharges; i++) {
            Charge charge = charges.get(i);
            System.out.println("Charge " + (i + 1) + ": Position (" + charge.getX() + ", " + charge.getY() + ", " + charge.getZ() + "), Value: " + charge.getValue());
            totalCharge += charge.getValue();
        }
        System.out.println("Total charge in the system: " + totalCharge + " C");
    }
    

    public Vector3D calculateElectricField(double x, double y, double z, int excludeChargeIndex) {
        Vector3D electricField = new Vector3D(0.0, 0.0, 0.0);

        for (int i = 0; i < numberOfCharges; i++) {
            if (i == excludeChargeIndex) {
                continue; 
            }
            Charge charge = charges.get(i);
            Vector3D r = new Vector3D(x - charge.getX(), y - charge.getY(), z - charge.getZ());
            double rSquared = r.magnitudeSquared();
            double electricFieldMagnitude = (Constants.K * charge.getValue()) / rSquared;
            Vector3D electricFieldContribution = r.normalize().scalarMultiply(electricFieldMagnitude);
            electricField = electricField.add(electricFieldContribution);
        }

        return electricField;
    }

    public Vector3D calculateForceOnCharge(int chargeIndex) {
        if (chargeIndex < 0 || chargeIndex >= numberOfCharges) {
            System.out.println("Invalid charge index.");
            return new Vector3D(0.0, 0.0, 0.0);
        }

        Vector3D force = new Vector3D(0.0, 0.0, 0.0);
        Charge targetCharge = charges.get(chargeIndex);

        for (int i = 0; i < numberOfCharges; i++) {
            if (i == chargeIndex) {
                continue; 
            }
            Charge charge = charges.get(i);
            Vector3D r = new Vector3D(targetCharge.getX() - charge.getX(), targetCharge.getY() - charge.getY(), targetCharge.getZ() - charge.getZ());
            double rSquared = r.magnitudeSquared();
            double forceMagnitude = (Constants.K * targetCharge.getValue() * charge.getValue()) / rSquared;
            Vector3D forceContribution = r.normalize().scalarMultiply(forceMagnitude);
            force = force.add(forceContribution);
        }

        return force;
    }

    public void printForcesOnCharges() {
        for (int i = 0; i < numberOfCharges; i++) {
            Vector3D force = calculateForceOnCharge(i);
            System.out.println("Force on Charge " + (i + 1) + ": (" + force.getX() + ", " + force.getY() + ", " + force.getZ() + ")");
        }
    }

    public void createChargeDistributionImage(String outputFilePath) {
        int imageWidth = 600; 
        int imageHeight = 600; 
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Draw x and y axes
       g2d.setColor(Color.BLACK);
       g2d.drawLine(0, imageHeight / 2, imageWidth, imageHeight / 2); // x-axis
       g2d.drawLine(imageWidth / 2, 0, imageWidth / 2, imageHeight); // y-axis

        for (Charge charge : charges) {
            double x = charge.getX();
            double y = charge.getY();
            double chargeValue = charge.getValue();

            int screenX = (int) (x * (imageWidth / 20.0) + imageWidth / 2.0);
            int screenY = (int) (-y * (imageHeight / 20.0) + imageHeight / 2.0);
 
            int chargeSize = (int) (Math.abs(chargeValue) * 10);

            Color chargeColor = (chargeValue > 0) ? Color.RED : Color.BLUE; 

            g2d.setColor(chargeColor);
            g2d.fillOval(screenX - chargeSize / 2, screenY - chargeSize / 2, chargeSize, chargeSize);
        }

        g2d.dispose();

        try {
            File outputFile = new File(outputFilePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    public static void main(String[] args) {
        ElectricChargeSystem chargeSystem = new ElectricChargeSystem(4);
    
        // Specify charge parameters for four charges
        chargeSystem.specifyChargeParameters(0, 0.0, -1.0, 0.0, 2.0); // 2 uC at (0, -1, 0) meters
        chargeSystem.specifyChargeParameters(1, 0.0, 2.0, 0.0, -2.0); // -2 uC at (0, 2, 0) meters
        chargeSystem.specifyChargeParameters(2, 1.0, -1.0, 0.0, 1.0);  // 1 uC at (1, -1, 0) meters
        chargeSystem.specifyChargeParameters(3, 1.0, 1.0, 0.0, -1.0);  // -1 uC at (1, 1, 0) meters
    
        // Print the values of the charges and the total charge in the system
        chargeSystem.printChargesAndTotalCharge();
    
        // Print the forces acting on each charge
        chargeSystem.printForcesOnCharges();
    
        // Create the charge distribution image
        chargeSystem.createChargeDistributionImage("charge_distribution.png");
        System.out.println("PNG file created in the program folder");
    }
    
}

class Charge {
    private double x;
    private double y;
    private double z;
    private double value;

    public Charge(double x, double y, double z, double value) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getValue() {
        return value;
    }
}

class Vector3D {
    private double x;
    private double y;
    private double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3D normalize() {
        double magnitude = Math.sqrt(magnitudeSquared());
        return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
    }

    public Vector3D scalarMultiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }

    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.getX(), y + other.getY(), z + other.getZ());
    }
}

class Constants {
    public static final double K = 8.9875e9;
}
