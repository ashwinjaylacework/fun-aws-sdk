# fun-aws-sdk

# What?
This is a quick and dirty, poorly written demo, with no tests.

The goal is to see if the HTTP response from AWS, received by the AWS Java SDK can be captured
directly into a file, while also letting the calling program run unaffected and 
unaware of the interception.
  
# Why?
The reason this could be useful is that the output of the HTTP response can be stored to the
file in its raw form, without having to go through the process of marshalling/serializing it again.
This saves the round trip of 
`HTTP response -> XML/JSON unmarshalling -> Java objects -> JSON marshalling ->File output`.

Not only would it be performant (?) but also avoids having to rely on the Java SDK's 
serialization format and stay as close to the Python or AWS CLI as possible. 
                       
## Sample output

```shell
> ls

s3-bucket-policy-1975-lw-kops-state-dev1.json
s3-bucket-policy-1975-lw-kops-state-dev2.json
s3-bucket-policy-1975-lw-kops-state-dev3.json
s3-bucket-policy-1975-lw-kops-state-dev4.json
s3-bucket-policy-1975-lw-kops-state-dev5.json
s3-bucket-policy-1975-lw-kops-state-devperf1.json
s3-bucket-policy-1975-lw-kops-state-devtest-mgmt.json
s3-bucket-policy-1975-lw-kops-state-devtest.json
s3-bucket-policy-1975-lw-kops-state-preprod1.json
s3-bucket-policy-1975-lw-kops-state-qa10.json
s3-bucket-policy-1975-lw-kops-state-qa11.json
s3-bucket-policy-1975-lw-kops-state-qa2.json
s3-bucket-policy-1975-lw-kops-state-qa3.json
s3-bucket-policy-1975-lw-kops-state-qa4.json
s3-bucket-policy-1975-lw-kops-state-qa5.json
s3-bucket-policy-1975-lw-kops-state-qa7.json
s3-bucket-policy-1975-lw-kops-state-qa8.json
s3-bucket-policy-1975-lw-kops-state-qa9.json

s3-list-buckets.xml
```

```xml
> cat s3-list-buckets.xml

<?xml version="1.0" encoding="UTF-8"?>
<ListAllMyBucketsResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/"><Owner><ID>1f042068a1c3d7b9c45d7921055d35ede77ae41ff2f4ed7797d031e42ddae2e9</ID><DisplayName>awsdevtest-admin</DisplayName></Owner><Buckets><Bucket><Name>1975-lw-kops-state-dev1</Name><CreationDat...</Bucket></Buckets></ListAllMyBucketsResult>`
```

```json
> cat s3-bucket-policy-1975-lw-kops-state-dev1.json

{"Version":"2012-10-17","Statement":[{"Sid":"s3-deny-non-secure-access","Ef...":{"Bool":{"aws:SecureTransport":"false"}}}]}
```