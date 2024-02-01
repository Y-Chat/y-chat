package ychat.socialservice.model.group;

public class GroupProfileBuilder {
    private String groupName = "GroopGroop";

    public GroupProfileBuilder withGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public GroupProfile build() {
        return new GroupProfile(groupName);
    }
}
