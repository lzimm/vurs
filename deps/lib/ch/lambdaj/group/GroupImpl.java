// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.group;

import static ch.lambdaj.Lambda.*;

import java.util.*;

/**
 * The standard LambdaJ implementation for the Group interface
 * @author Mario Fusco
 */
public class GroupImpl<T> extends ArrayList<GroupItem<T>> implements Group<T> {

	private static final long serialVersionUID = 1L;

	private final Map<String, GroupItem<T>> groupsMap = new HashMap<String, GroupItem<T>>();

	private transient GroupCondition<?> groupCondition;

	protected GroupImpl() { }

	public GroupImpl(GroupCondition<?> groupCondition) {
		this.groupCondition = groupCondition;
	}

	void addItem(T item) {
		GroupItem<T> groupItem = findOrCreate(item, groupCondition.getGroupValue(item));
		groupItem.addChild(item);
	}

	private GroupItem<T> findOrCreate(T item, Object key) {
        String keyAsString = key == null ? "" : key.toString();
		GroupItem<T> groupItem = groupsMap.get(keyAsString);
		return groupItem != null ? groupItem : create(item, key, keyAsString);
	}

	private GroupItem<T> create(T item, Object key, String keyAsString) {
        GroupItem<T> groupItem = groupCondition.create(item, key, keyAsString);
        groupsMap.put(keyAsString, groupItem);
        add(groupItem);
        return groupItem;
	}
	
	public Set<String> keySet() {
		return groupsMap.keySet();
	}

	public Group<T> findGroup(String key) {
		GroupItem<T> groupItem = groupsMap.get(key);
		return groupItem == null ? null : groupItem.asGroup();
	}

	public Group<T> findGroup(Object key) {
		return findGroup(key.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<Group<T>> subgroups() {
		return (List<Group<T>>)collect(forEach(this, GroupItem.class).asGroup());
	}
	
	public List<T> find(String key) {
		GroupItem<T> groupItem = groupsMap.get(key);
		return groupItem == null ? new LinkedList<T>() : groupItem.asList();
	}

	public List<T> find(Object key) {
		return find(key.toString());
	}
	
	public List<T> findAll() {
		List<T> allItems = new LinkedList<T>();
		for (GroupItem<T> groupItem : this) allItems.addAll(groupItem.asList());
		return allItems;
	}
	
	public int getSize() {
		return findAll().size();
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public Set<String> getHeads() {
		return new HashSet<String>();
	}

	public String getHeadValue(String key) {
		return "";
	}
}
