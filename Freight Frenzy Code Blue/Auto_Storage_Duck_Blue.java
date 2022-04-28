package org.firstinspires.ftc.teamcode.Blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Auto_Storage_Duck_Blue")
public class Auto_Storage_Duck_Blue extends LinearOpMode {
  private Blinker control_Hub;
  private Blinker expansion_Hub;
  private DcMotor leftRear;
  private DcMotor leftFront;
  private DcMotor rightRear;
  private DcMotor rightFront;
  private DcMotor armDrive;
  private DcMotor carousel;
  private DcMotor intake;
  
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
    while (opModeIsActive() && runtime.seconds() <= 4) {
      // set target position
      armDrive.setTargetPosition(armPos);
      //switch to run to position mode
      armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      //run to position at the desiginated power
      armDrive.setPower(0.5);
      //run intake to hold freight in position
      intake.setPower(0.3);
     
      }
      
      while (opModeIsActive() && runtime.seconds() > 4 && runtime.seconds() <= 8) {
        
          //run the intake to score the preload
          intake.setPower(-0.3);
       }
      
    while (opModeIsActive() && runtime.seconds() > 8 && runtime.seconds() <= 10) {
      
      //stop the intake
      intake.setPower(0);
      
      // set target position
      armDrive.setTargetPosition(-50);
      //switch to run to position mode
      armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      //run to position at the desiginated power
      armDrive.setPower(0.5);
    }
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


  @Override
  public void runOpMode() {

    leftRear = hardwareMap.get(DcMotor.class, "leftRear");
    leftFront = hardwareMap.get(DcMotor.class, "leftFront");
    rightRear = hardwareMap.get(DcMotor.class, "rightRear");
    rightFront = hardwareMap.get(DcMotor.class, "rightFront");
    armDrive = hardwareMap.get(DcMotor.class, "armDrive");
    intake = hardwareMap.get(DcMotor.class, "intake");
    carousel = hardwareMap.get(DcMotor.class, "carousel");
    
    leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
    leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
    
    waitForStart();
    if (opModeIsActive()) {
      
      armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
          
       //segment 1 - back off wall
        drive(0.5, -10, -10, -10, -10);

       //segment 2 - turn to face carousel 
        drive(0.5, -9, -9, 9, 9);
      
        //segment 3 - drive to carousel
        drive(0.5, 9, 9, 9, 9);
        
        //segment 4 - score the duck
        duck(0.24);
        
        //segment 5 - back off carousel
        drive(0.5, -2, -2, -2, -2);
        
        //segment 6 - turn for parking
        drive(0.5, 9, 9, -9, -9);
        
        //segment 7 - back into the storage unit
        drive(0.5, -22, -22, -22, -22);
        
        //segment 8 - strafe right to get in storage
        drive(0.5, 5, -5, -5, 5);
        
   
  }
 }
}
