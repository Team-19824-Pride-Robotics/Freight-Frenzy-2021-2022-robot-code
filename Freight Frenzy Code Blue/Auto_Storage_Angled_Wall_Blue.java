package org.firstinspires.ftc.teamcode.Blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous(name = "Auto_Storage_Angled_Wall_Blue")
public class Auto_Storage_Angled_Wall_Blue extends LinearOpMode {
  private Blinker control_Hub;
  private Blinker expansion_Hub;
  private DcMotor leftRear;
  private DcMotor leftFront;
  private DcMotor rightRear;
  private DcMotor rightFront;
  private DcMotor armDrive;
  private DcMotor carousel;
  private DcMotor intake;
  private HardwareDevice webcam_1;
  
      
  private static final String TFOD_MODEL_ASSET = "/sdcard/FIRST/tflitemodels/model_20220218_125207.tflite";
  private static final String[] LABELS = {
      "Red Cup"
    };

  private static final String VUFORIA_KEY =
            "ATikKv7/////AAABmZEp8imS2kWSho7r5LLtLJ8aIM3qubW0EhIhJbicruc1o7KMC59JuCMVgBYtWWPQmuIcP1orD6ULF8fDXHrt1+efUVLBiAlMnhdv0PFhCbIacnvbZyiw2xgbwH6E+fSpZuEzJGphHvkW2RXDMLmbrhCpHdGOK6zN75R8o5ZQ5JyGinVvFF8Y7/4tCEiqPH+cNiv4xtX3TthzBWj6CzZGreirC9SjAJLdXJO9WwTV7y0Yoh0IntB5CdC9EMrr+koPH5h0RjOv5qC3KgP6ulmLYQJLVeHUyZhFQuGuO/Wu0K3/ARiOlF+jYWyU7yP5unMJAF3BfnFBN1B0/3OIAos1zKprKMiN69RrGNckA0xK8IFd";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
  private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
  private TFObjectDetector tfod;
  
  //Convert from the counts per revolution of the encoder to counts per inch
  static final double HD_COUNTS_PER_REV = 28;
  static final double DRIVE_GEAR_REDUCTION = 19.2;
  static final double WHEEL_CIRCUMFERENCE_MM = 96 * Math.PI;
  static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
  static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;
  
  //Create elapsed time variable and an instance of elapsed time
  private ElapsedTime     runtime = new ElapsedTime();
  
  
  // Drive function with 3 parameters
  private void drive(double power, double leftInches, double leftInches2, double rightInches, double rightInches2) {
    int leftTarget;
    int leftTarget2;
    int rightTarget;
    int rightTarget2;

    if (opModeIsActive()) {
     // Create target positions
      leftTarget = leftRear.getCurrentPosition() + (int)(leftInches * DRIVE_COUNTS_PER_IN);
      leftTarget2 = leftFront.getCurrentPosition() + (int)(leftInches2 * DRIVE_COUNTS_PER_IN);
      rightTarget = rightRear.getCurrentPosition() + (int)(rightInches * DRIVE_COUNTS_PER_IN);
      rightTarget2 = rightFront.getCurrentPosition() + (int)(rightInches2 * DRIVE_COUNTS_PER_IN);
      
      // set target position
      leftRear.setTargetPosition(leftTarget);
      leftFront.setTargetPosition(leftTarget2);
      rightRear.setTargetPosition(rightTarget);
      rightFront.setTargetPosition(rightTarget2);
      
      //switch to run to position mode
      leftRear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      rightRear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      
      //run to position at the desiginated power
      leftRear.setPower(power);
      leftFront.setPower(power);
      rightRear.setPower(power);
      rightFront.setPower(power);
      
      // wait until all motors are no longer busy running to position
      while (opModeIsActive() && (leftRear.isBusy() || rightRear.isBusy() || leftFront.isBusy() || rightFront.isBusy())) {
      }
      
      // set motor power back to 0
      leftRear.setPower(0);
      leftFront.setPower(0);
      rightRear.setPower(0);
      rightFront.setPower(0);
    }
  }
  
   // score function with 1 parameters
   private void score(int armPos) {
    
    // reset elapsed time timer
    runtime.reset(); 
    while (opModeIsActive() && runtime.seconds() <= 2) {
      // set target position
      armDrive.setTargetPosition(armPos);
      //switch to run to position mode
      armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      //run to position at the desiginated power
      armDrive.setPower(0.5);
      //run intake to hold freight in position
      intake.setPower(0.3);
     
      }
      
      while (opModeIsActive() && runtime.seconds() > 2 && runtime.seconds() <= 3) {
        
          //run the intake to score the preload
          intake.setPower(-0.4);
       }
      
    while (opModeIsActive() && runtime.seconds() > 3 && runtime.seconds() <= 4) {
      
      //stop the intake
      intake.setPower(0);
      
      // set target position
      armDrive.setTargetPosition(-50);
      //switch to run to position mode
      armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      //run to position at the desiginated power
      armDrive.setPower(0.5);
    }
                 while (opModeIsActive() && runtime.seconds() > 4 && runtime.seconds() <= 5) {
      
      //stop the arm
      armDrive.setPower(0);
         }
  }
  
//function for finding the cup
     private int lookForCup() {

        int value = -1060; 

     // reset elapsed time timer
        runtime.reset(); 
    
        while (opModeIsActive() && runtime.seconds() <= 3) {

      //look for cup and return a value based on what you see
          if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());
                      // step through the list of recognitions and display boundary info.
                      int i = 0;
                      for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());

                        if (recognition.getLeft() > 100) {
                            
                          value = -1200; 
                        }
                        else {
                            
                          value = -1350;
                        }
                        // else if (recognition.getLeft() >100) {
                        //   value = -1000; //need lower level go higher** //1200
                        // }
                    
                        i++;
                      }
                      telemetry.update();
                    }
                }
        }
      return value;
     }
private void duck(double duckSpeed) {
    
    // reset elapsed time timer
    runtime.reset(); 
    
    while (opModeIsActive() && runtime.seconds() <= 6) {
      
        carousel.setPower(duckSpeed);

      }
      
    while (opModeIsActive() && runtime.seconds() > 6 && runtime.seconds() <= 8) {
      
      //stop the carousel
      carousel.setPower(0);

      }
  }
private void pause(double time) {
    
    // reset elapsed time timer
    runtime.reset(); 
    
    while (opModeIsActive() && runtime.seconds() <= time) {
      
        carousel.setPower(0.1);

      }
      
    while (opModeIsActive() && runtime.seconds() > time && runtime.seconds() <= (time+1)) {
      
      //stop the carousel
      carousel.setPower(0);

      }
  }
  @Override
  public void runOpMode() {
   
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();
            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1, 16.0/9.0);
        }
        
       

    leftRear = hardwareMap.get(DcMotor.class, "leftRear");
    leftFront = hardwareMap.get(DcMotor.class, "leftFront");
    rightRear = hardwareMap.get(DcMotor.class, "rightRear");
    rightFront = hardwareMap.get(DcMotor.class, "rightFront");
    armDrive = hardwareMap.get(DcMotor.class, "armDrive");
    intake = hardwareMap.get(DcMotor.class, "intake");
    carousel = hardwareMap.get(DcMotor.class, "carousel");
    
    leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
    leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
    
    /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();
        
    if (opModeIsActive()) {
      
       armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       
       //change this value if the robot needs to drive up to the hub a different amount
       double toHub = 8.5;

        //start directly in front of the markers 
       
       
       //segment 1 - look for the cup and record its value in a variable
     int cup = lookForCup();   
     if (cup > 1100) {
            if (cup > 1250) {
              toHub = 8.5;
            }
            else {
              toHub = 8.5;
            }
          }
     
       //segment 2 - drive to the markers
        drive(0.3, 6, 6, 6, 6);
     
       //segment 3 - turn left towards the shipping hub
        drive(0.5, -6, -6, 6, 6);
        
      //segment 4 - drive forward to hub
        drive(0.3, toHub, toHub, toHub, toHub);
        
      //segment 5 - score the pre-load at the pre-determined level
        score(cup);
     
       //segment 6 - turn to face warehouse 
        drive(0.5, -11, -11, 11, 11);
        
       //segeent 7 - ram into the wall 
        drive(0.7, 25, -25, -25, 25);
       
        //segment 8 - drive into the warehouse
        drive(1, 85, 85, 85, 85);
  }
 }
     /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.7f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromFile(TFOD_MODEL_ASSET, LABELS);
    }
}
