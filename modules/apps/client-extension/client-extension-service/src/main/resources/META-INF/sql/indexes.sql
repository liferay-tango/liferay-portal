create index IX_CA514799 on ClientExtensionEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_7CDD4FB0 on ClientExtensionEntry (companyId, iFrameURL[$COLUMN_LENGTH:4000$]);
create index IX_F8DF9578 on ClientExtensionEntry (uuid_[$COLUMN_LENGTH:75$], companyId);