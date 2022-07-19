/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.afarcloud.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum VehicleType implements org.apache.thrift.TEnum {
  AUAV(0),
  RUAV(1),
  AGV(2),
  RGV(3),
  UAV(4),
  UGV(5),
  Tractor(6);

  private final int value;

  private VehicleType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static VehicleType findByValue(int value) { 
    switch (value) {
      case 0:
        return AUAV;
      case 1:
        return RUAV;
      case 2:
        return AGV;
      case 3:
        return RGV;
      case 4:
        return UAV;
      case 5:
        return UGV;
      case 6:
        return Tractor;
      default:
        return null;
    }
  }
}