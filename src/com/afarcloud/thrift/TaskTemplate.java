/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.afarcloud.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2021-1-6")
public class TaskTemplate implements org.apache.thrift.TBase<TaskTemplate, TaskTemplate._Fields>, java.io.Serializable, Cloneable, Comparable<TaskTemplate> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TaskTemplate");

  private static final org.apache.thrift.protocol.TField TASK_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("taskType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("description", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField REGION_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("regionType", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField REQUIRED_TYPES_FIELD_DESC = new org.apache.thrift.protocol.TField("requiredTypes", org.apache.thrift.protocol.TType.LIST, (short)4);
  private static final org.apache.thrift.protocol.TField MAX_SPEED_FIELD_DESC = new org.apache.thrift.protocol.TField("maxSpeed", org.apache.thrift.protocol.TType.DOUBLE, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TaskTemplateStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TaskTemplateTupleSchemeFactory());
  }

  /**
   * 
   * @see TaskType
   */
  public TaskType taskType; // required
  public String description; // required
  /**
   * 
   * @see TaskRegionType
   */
  public TaskRegionType regionType; // required
  public List<EquipmentType> requiredTypes; // required
  public double maxSpeed; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see TaskType
     */
    TASK_TYPE((short)1, "taskType"),
    DESCRIPTION((short)2, "description"),
    /**
     * 
     * @see TaskRegionType
     */
    REGION_TYPE((short)3, "regionType"),
    REQUIRED_TYPES((short)4, "requiredTypes"),
    MAX_SPEED((short)5, "maxSpeed");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TASK_TYPE
          return TASK_TYPE;
        case 2: // DESCRIPTION
          return DESCRIPTION;
        case 3: // REGION_TYPE
          return REGION_TYPE;
        case 4: // REQUIRED_TYPES
          return REQUIRED_TYPES;
        case 5: // MAX_SPEED
          return MAX_SPEED;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __MAXSPEED_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.MAX_SPEED};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TASK_TYPE, new org.apache.thrift.meta_data.FieldMetaData("taskType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TaskType.class)));
    tmpMap.put(_Fields.DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("description", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.REGION_TYPE, new org.apache.thrift.meta_data.FieldMetaData("regionType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TaskRegionType.class)));
    tmpMap.put(_Fields.REQUIRED_TYPES, new org.apache.thrift.meta_data.FieldMetaData("requiredTypes", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, EquipmentType.class))));
    tmpMap.put(_Fields.MAX_SPEED, new org.apache.thrift.meta_data.FieldMetaData("maxSpeed", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TaskTemplate.class, metaDataMap);
  }

  public TaskTemplate() {
  }

  public TaskTemplate(
    TaskType taskType,
    String description,
    TaskRegionType regionType,
    List<EquipmentType> requiredTypes)
  {
    this();
    this.taskType = taskType;
    this.description = description;
    this.regionType = regionType;
    this.requiredTypes = requiredTypes;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TaskTemplate(TaskTemplate other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetTaskType()) {
      this.taskType = other.taskType;
    }
    if (other.isSetDescription()) {
      this.description = other.description;
    }
    if (other.isSetRegionType()) {
      this.regionType = other.regionType;
    }
    if (other.isSetRequiredTypes()) {
      List<EquipmentType> __this__requiredTypes = new ArrayList<EquipmentType>(other.requiredTypes.size());
      for (EquipmentType other_element : other.requiredTypes) {
        __this__requiredTypes.add(other_element);
      }
      this.requiredTypes = __this__requiredTypes;
    }
    this.maxSpeed = other.maxSpeed;
  }

  public TaskTemplate deepCopy() {
    return new TaskTemplate(this);
  }

  @Override
  public void clear() {
    this.taskType = null;
    this.description = null;
    this.regionType = null;
    this.requiredTypes = null;
    setMaxSpeedIsSet(false);
    this.maxSpeed = 0.0;
  }

  /**
   * 
   * @see TaskType
   */
  public TaskType getTaskType() {
    return this.taskType;
  }

  /**
   * 
   * @see TaskType
   */
  public TaskTemplate setTaskType(TaskType taskType) {
    this.taskType = taskType;
    return this;
  }

  public void unsetTaskType() {
    this.taskType = null;
  }

  /** Returns true if field taskType is set (has been assigned a value) and false otherwise */
  public boolean isSetTaskType() {
    return this.taskType != null;
  }

  public void setTaskTypeIsSet(boolean value) {
    if (!value) {
      this.taskType = null;
    }
  }

  public String getDescription() {
    return this.description;
  }

  public TaskTemplate setDescription(String description) {
    this.description = description;
    return this;
  }

  public void unsetDescription() {
    this.description = null;
  }

  /** Returns true if field description is set (has been assigned a value) and false otherwise */
  public boolean isSetDescription() {
    return this.description != null;
  }

  public void setDescriptionIsSet(boolean value) {
    if (!value) {
      this.description = null;
    }
  }

  /**
   * 
   * @see TaskRegionType
   */
  public TaskRegionType getRegionType() {
    return this.regionType;
  }

  /**
   * 
   * @see TaskRegionType
   */
  public TaskTemplate setRegionType(TaskRegionType regionType) {
    this.regionType = regionType;
    return this;
  }

  public void unsetRegionType() {
    this.regionType = null;
  }

  /** Returns true if field regionType is set (has been assigned a value) and false otherwise */
  public boolean isSetRegionType() {
    return this.regionType != null;
  }

  public void setRegionTypeIsSet(boolean value) {
    if (!value) {
      this.regionType = null;
    }
  }

  public int getRequiredTypesSize() {
    return (this.requiredTypes == null) ? 0 : this.requiredTypes.size();
  }

  public java.util.Iterator<EquipmentType> getRequiredTypesIterator() {
    return (this.requiredTypes == null) ? null : this.requiredTypes.iterator();
  }

  public void addToRequiredTypes(EquipmentType elem) {
    if (this.requiredTypes == null) {
      this.requiredTypes = new ArrayList<EquipmentType>();
    }
    this.requiredTypes.add(elem);
  }

  public List<EquipmentType> getRequiredTypes() {
    return this.requiredTypes;
  }

  public TaskTemplate setRequiredTypes(List<EquipmentType> requiredTypes) {
    this.requiredTypes = requiredTypes;
    return this;
  }

  public void unsetRequiredTypes() {
    this.requiredTypes = null;
  }

  /** Returns true if field requiredTypes is set (has been assigned a value) and false otherwise */
  public boolean isSetRequiredTypes() {
    return this.requiredTypes != null;
  }

  public void setRequiredTypesIsSet(boolean value) {
    if (!value) {
      this.requiredTypes = null;
    }
  }

  public double getMaxSpeed() {
    return this.maxSpeed;
  }

  public TaskTemplate setMaxSpeed(double maxSpeed) {
    this.maxSpeed = maxSpeed;
    setMaxSpeedIsSet(true);
    return this;
  }

  public void unsetMaxSpeed() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MAXSPEED_ISSET_ID);
  }

  /** Returns true if field maxSpeed is set (has been assigned a value) and false otherwise */
  public boolean isSetMaxSpeed() {
    return EncodingUtils.testBit(__isset_bitfield, __MAXSPEED_ISSET_ID);
  }

  public void setMaxSpeedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MAXSPEED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TASK_TYPE:
      if (value == null) {
        unsetTaskType();
      } else {
        setTaskType((TaskType)value);
      }
      break;

    case DESCRIPTION:
      if (value == null) {
        unsetDescription();
      } else {
        setDescription((String)value);
      }
      break;

    case REGION_TYPE:
      if (value == null) {
        unsetRegionType();
      } else {
        setRegionType((TaskRegionType)value);
      }
      break;

    case REQUIRED_TYPES:
      if (value == null) {
        unsetRequiredTypes();
      } else {
        setRequiredTypes((List<EquipmentType>)value);
      }
      break;

    case MAX_SPEED:
      if (value == null) {
        unsetMaxSpeed();
      } else {
        setMaxSpeed((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TASK_TYPE:
      return getTaskType();

    case DESCRIPTION:
      return getDescription();

    case REGION_TYPE:
      return getRegionType();

    case REQUIRED_TYPES:
      return getRequiredTypes();

    case MAX_SPEED:
      return Double.valueOf(getMaxSpeed());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TASK_TYPE:
      return isSetTaskType();
    case DESCRIPTION:
      return isSetDescription();
    case REGION_TYPE:
      return isSetRegionType();
    case REQUIRED_TYPES:
      return isSetRequiredTypes();
    case MAX_SPEED:
      return isSetMaxSpeed();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TaskTemplate)
      return this.equals((TaskTemplate)that);
    return false;
  }

  public boolean equals(TaskTemplate that) {
    if (that == null)
      return false;

    boolean this_present_taskType = true && this.isSetTaskType();
    boolean that_present_taskType = true && that.isSetTaskType();
    if (this_present_taskType || that_present_taskType) {
      if (!(this_present_taskType && that_present_taskType))
        return false;
      if (!this.taskType.equals(that.taskType))
        return false;
    }

    boolean this_present_description = true && this.isSetDescription();
    boolean that_present_description = true && that.isSetDescription();
    if (this_present_description || that_present_description) {
      if (!(this_present_description && that_present_description))
        return false;
      if (!this.description.equals(that.description))
        return false;
    }

    boolean this_present_regionType = true && this.isSetRegionType();
    boolean that_present_regionType = true && that.isSetRegionType();
    if (this_present_regionType || that_present_regionType) {
      if (!(this_present_regionType && that_present_regionType))
        return false;
      if (!this.regionType.equals(that.regionType))
        return false;
    }

    boolean this_present_requiredTypes = true && this.isSetRequiredTypes();
    boolean that_present_requiredTypes = true && that.isSetRequiredTypes();
    if (this_present_requiredTypes || that_present_requiredTypes) {
      if (!(this_present_requiredTypes && that_present_requiredTypes))
        return false;
      if (!this.requiredTypes.equals(that.requiredTypes))
        return false;
    }

    boolean this_present_maxSpeed = true && this.isSetMaxSpeed();
    boolean that_present_maxSpeed = true && that.isSetMaxSpeed();
    if (this_present_maxSpeed || that_present_maxSpeed) {
      if (!(this_present_maxSpeed && that_present_maxSpeed))
        return false;
      if (this.maxSpeed != that.maxSpeed)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_taskType = true && (isSetTaskType());
    list.add(present_taskType);
    if (present_taskType)
      list.add(taskType.getValue());

    boolean present_description = true && (isSetDescription());
    list.add(present_description);
    if (present_description)
      list.add(description);

    boolean present_regionType = true && (isSetRegionType());
    list.add(present_regionType);
    if (present_regionType)
      list.add(regionType.getValue());

    boolean present_requiredTypes = true && (isSetRequiredTypes());
    list.add(present_requiredTypes);
    if (present_requiredTypes)
      list.add(requiredTypes);

    boolean present_maxSpeed = true && (isSetMaxSpeed());
    list.add(present_maxSpeed);
    if (present_maxSpeed)
      list.add(maxSpeed);

    return list.hashCode();
  }

  @Override
  public int compareTo(TaskTemplate other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTaskType()).compareTo(other.isSetTaskType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaskType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taskType, other.taskType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDescription()).compareTo(other.isSetDescription());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDescription()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.description, other.description);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRegionType()).compareTo(other.isSetRegionType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRegionType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.regionType, other.regionType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRequiredTypes()).compareTo(other.isSetRequiredTypes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRequiredTypes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.requiredTypes, other.requiredTypes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMaxSpeed()).compareTo(other.isSetMaxSpeed());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMaxSpeed()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.maxSpeed, other.maxSpeed);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TaskTemplate(");
    boolean first = true;

    sb.append("taskType:");
    if (this.taskType == null) {
      sb.append("null");
    } else {
      sb.append(this.taskType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("description:");
    if (this.description == null) {
      sb.append("null");
    } else {
      sb.append(this.description);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("regionType:");
    if (this.regionType == null) {
      sb.append("null");
    } else {
      sb.append(this.regionType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("requiredTypes:");
    if (this.requiredTypes == null) {
      sb.append("null");
    } else {
      sb.append(this.requiredTypes);
    }
    first = false;
    if (isSetMaxSpeed()) {
      if (!first) sb.append(", ");
      sb.append("maxSpeed:");
      sb.append(this.maxSpeed);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TaskTemplateStandardSchemeFactory implements SchemeFactory {
    public TaskTemplateStandardScheme getScheme() {
      return new TaskTemplateStandardScheme();
    }
  }

  private static class TaskTemplateStandardScheme extends StandardScheme<TaskTemplate> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TaskTemplate struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TASK_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.taskType = com.afarcloud.thrift.TaskType.findByValue(iprot.readI32());
              struct.setTaskTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DESCRIPTION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.description = iprot.readString();
              struct.setDescriptionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // REGION_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.regionType = com.afarcloud.thrift.TaskRegionType.findByValue(iprot.readI32());
              struct.setRegionTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // REQUIRED_TYPES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list24 = iprot.readListBegin();
                struct.requiredTypes = new ArrayList<EquipmentType>(_list24.size);
                EquipmentType _elem25;
                for (int _i26 = 0; _i26 < _list24.size; ++_i26)
                {
                  _elem25 = com.afarcloud.thrift.EquipmentType.findByValue(iprot.readI32());
                  struct.requiredTypes.add(_elem25);
                }
                iprot.readListEnd();
              }
              struct.setRequiredTypesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // MAX_SPEED
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.maxSpeed = iprot.readDouble();
              struct.setMaxSpeedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TaskTemplate struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.taskType != null) {
        oprot.writeFieldBegin(TASK_TYPE_FIELD_DESC);
        oprot.writeI32(struct.taskType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.description != null) {
        oprot.writeFieldBegin(DESCRIPTION_FIELD_DESC);
        oprot.writeString(struct.description);
        oprot.writeFieldEnd();
      }
      if (struct.regionType != null) {
        oprot.writeFieldBegin(REGION_TYPE_FIELD_DESC);
        oprot.writeI32(struct.regionType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.requiredTypes != null) {
        oprot.writeFieldBegin(REQUIRED_TYPES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.requiredTypes.size()));
          for (EquipmentType _iter27 : struct.requiredTypes)
          {
            oprot.writeI32(_iter27.getValue());
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.isSetMaxSpeed()) {
        oprot.writeFieldBegin(MAX_SPEED_FIELD_DESC);
        oprot.writeDouble(struct.maxSpeed);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TaskTemplateTupleSchemeFactory implements SchemeFactory {
    public TaskTemplateTupleScheme getScheme() {
      return new TaskTemplateTupleScheme();
    }
  }

  private static class TaskTemplateTupleScheme extends TupleScheme<TaskTemplate> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TaskTemplate struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetTaskType()) {
        optionals.set(0);
      }
      if (struct.isSetDescription()) {
        optionals.set(1);
      }
      if (struct.isSetRegionType()) {
        optionals.set(2);
      }
      if (struct.isSetRequiredTypes()) {
        optionals.set(3);
      }
      if (struct.isSetMaxSpeed()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetTaskType()) {
        oprot.writeI32(struct.taskType.getValue());
      }
      if (struct.isSetDescription()) {
        oprot.writeString(struct.description);
      }
      if (struct.isSetRegionType()) {
        oprot.writeI32(struct.regionType.getValue());
      }
      if (struct.isSetRequiredTypes()) {
        {
          oprot.writeI32(struct.requiredTypes.size());
          for (EquipmentType _iter28 : struct.requiredTypes)
          {
            oprot.writeI32(_iter28.getValue());
          }
        }
      }
      if (struct.isSetMaxSpeed()) {
        oprot.writeDouble(struct.maxSpeed);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TaskTemplate struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.taskType = com.afarcloud.thrift.TaskType.findByValue(iprot.readI32());
        struct.setTaskTypeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.description = iprot.readString();
        struct.setDescriptionIsSet(true);
      }
      if (incoming.get(2)) {
        struct.regionType = com.afarcloud.thrift.TaskRegionType.findByValue(iprot.readI32());
        struct.setRegionTypeIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list29 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.requiredTypes = new ArrayList<EquipmentType>(_list29.size);
          EquipmentType _elem30;
          for (int _i31 = 0; _i31 < _list29.size; ++_i31)
          {
            _elem30 = com.afarcloud.thrift.EquipmentType.findByValue(iprot.readI32());
            struct.requiredTypes.add(_elem30);
          }
        }
        struct.setRequiredTypesIsSet(true);
      }
      if (incoming.get(4)) {
        struct.maxSpeed = iprot.readDouble();
        struct.setMaxSpeedIsSet(true);
      }
    }
  }

}
