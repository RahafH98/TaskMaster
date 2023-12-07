package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTeam", fields = {"teamId","title"})
public final class Task implements Model {
  public static final QueryField ID = field("Task", "id");
  public static final QueryField TITLE = field("Task", "title");
  public static final QueryField BODY = field("Task", "body");
  public static final QueryField DATE_CREATED = field("Task", "dateCreated");
  public static final QueryField STATE = field("Task", "state");
  public static final QueryField TEAM_TASK = field("Task", "teamId");
  public static final QueryField TASK_S3_URI = field("Task", "taskS3Uri");
  public static final QueryField TASK_LATITUDE = field("Task", "taskLatitude");
  public static final QueryField TASK_LONGITUDE = field("Task", "taskLongitude");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String") String body;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime dateCreated;
  private final @ModelField(targetType="State") State state;
  private final @ModelField(targetType="Team") @BelongsTo(targetName = "teamId", type = Team.class) Team teamTask;
  private final @ModelField(targetType="String") String taskS3Uri;
  private final @ModelField(targetType="String") String taskLatitude;
  private final @ModelField(targetType="String") String taskLongitude;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getBody() {
      return body;
  }
  
  public Temporal.DateTime getDateCreated() {
      return dateCreated;
  }
  
  public State getState() {
      return state;
  }
  
  public Team getTeamTask() {
      return teamTask;
  }
  
  public String getTaskS3Uri() {
      return taskS3Uri;
  }
  
  public String getTaskLatitude() {
      return taskLatitude;
  }
  
  public String getTaskLongitude() {
      return taskLongitude;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Task(String id, String title, String body, Temporal.DateTime dateCreated, State state, Team teamTask, String taskS3Uri, String taskLatitude, String taskLongitude) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.dateCreated = dateCreated;
    this.state = state;
    this.teamTask = teamTask;
    this.taskS3Uri = taskS3Uri;
    this.taskLatitude = taskLatitude;
    this.taskLongitude = taskLongitude;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Task task = (Task) obj;
      return ObjectsCompat.equals(getId(), task.getId()) &&
              ObjectsCompat.equals(getTitle(), task.getTitle()) &&
              ObjectsCompat.equals(getBody(), task.getBody()) &&
              ObjectsCompat.equals(getDateCreated(), task.getDateCreated()) &&
              ObjectsCompat.equals(getState(), task.getState()) &&
              ObjectsCompat.equals(getTeamTask(), task.getTeamTask()) &&
              ObjectsCompat.equals(getTaskS3Uri(), task.getTaskS3Uri()) &&
              ObjectsCompat.equals(getTaskLatitude(), task.getTaskLatitude()) &&
              ObjectsCompat.equals(getTaskLongitude(), task.getTaskLongitude()) &&
              ObjectsCompat.equals(getCreatedAt(), task.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), task.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getBody())
      .append(getDateCreated())
      .append(getState())
      .append(getTeamTask())
      .append(getTaskS3Uri())
      .append(getTaskLatitude())
      .append(getTaskLongitude())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Task {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("body=" + String.valueOf(getBody()) + ", ")
      .append("dateCreated=" + String.valueOf(getDateCreated()) + ", ")
      .append("state=" + String.valueOf(getState()) + ", ")
      .append("teamTask=" + String.valueOf(getTeamTask()) + ", ")
      .append("taskS3Uri=" + String.valueOf(getTaskS3Uri()) + ", ")
      .append("taskLatitude=" + String.valueOf(getTaskLatitude()) + ", ")
      .append("taskLongitude=" + String.valueOf(getTaskLongitude()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Task justId(String id) {
    return new Task(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      body,
      dateCreated,
      state,
      teamTask,
      taskS3Uri,
      taskLatitude,
      taskLongitude);
  }
  public interface TitleStep {
    BuildStep title(String title);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id);
    BuildStep body(String body);
    BuildStep dateCreated(Temporal.DateTime dateCreated);
    BuildStep state(State state);
    BuildStep teamTask(Team teamTask);
    BuildStep taskS3Uri(String taskS3Uri);
    BuildStep taskLatitude(String taskLatitude);
    BuildStep taskLongitude(String taskLongitude);
  }
  

  public static class Builder implements TitleStep, BuildStep {
    private String id;
    private String title;
    private String body;
    private Temporal.DateTime dateCreated;
    private State state;
    private Team teamTask;
    private String taskS3Uri;
    private String taskLatitude;
    private String taskLongitude;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String body, Temporal.DateTime dateCreated, State state, Team teamTask, String taskS3Uri, String taskLatitude, String taskLongitude) {
      this.id = id;
      this.title = title;
      this.body = body;
      this.dateCreated = dateCreated;
      this.state = state;
      this.teamTask = teamTask;
      this.taskS3Uri = taskS3Uri;
      this.taskLatitude = taskLatitude;
      this.taskLongitude = taskLongitude;
    }
    
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          title,
          body,
          dateCreated,
          state,
          teamTask,
          taskS3Uri,
          taskLatitude,
          taskLongitude);
    }
    
    @Override
     public BuildStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public BuildStep body(String body) {
        this.body = body;
        return this;
    }
    
    @Override
     public BuildStep dateCreated(Temporal.DateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }
    
    @Override
     public BuildStep state(State state) {
        this.state = state;
        return this;
    }
    
    @Override
     public BuildStep teamTask(Team teamTask) {
        this.teamTask = teamTask;
        return this;
    }
    
    @Override
     public BuildStep taskS3Uri(String taskS3Uri) {
        this.taskS3Uri = taskS3Uri;
        return this;
    }
    
    @Override
     public BuildStep taskLatitude(String taskLatitude) {
        this.taskLatitude = taskLatitude;
        return this;
    }
    
    @Override
     public BuildStep taskLongitude(String taskLongitude) {
        this.taskLongitude = taskLongitude;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String title, String body, Temporal.DateTime dateCreated, State state, Team teamTask, String taskS3Uri, String taskLatitude, String taskLongitude) {
      super(id, title, body, dateCreated, state, teamTask, taskS3Uri, taskLatitude, taskLongitude);
      Objects.requireNonNull(title);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder body(String body) {
      return (CopyOfBuilder) super.body(body);
    }
    
    @Override
     public CopyOfBuilder dateCreated(Temporal.DateTime dateCreated) {
      return (CopyOfBuilder) super.dateCreated(dateCreated);
    }
    
    @Override
     public CopyOfBuilder state(State state) {
      return (CopyOfBuilder) super.state(state);
    }
    
    @Override
     public CopyOfBuilder teamTask(Team teamTask) {
      return (CopyOfBuilder) super.teamTask(teamTask);
    }
    
    @Override
     public CopyOfBuilder taskS3Uri(String taskS3Uri) {
      return (CopyOfBuilder) super.taskS3Uri(taskS3Uri);
    }
    
    @Override
     public CopyOfBuilder taskLatitude(String taskLatitude) {
      return (CopyOfBuilder) super.taskLatitude(taskLatitude);
    }
    
    @Override
     public CopyOfBuilder taskLongitude(String taskLongitude) {
      return (CopyOfBuilder) super.taskLongitude(taskLongitude);
    }
  }
}
