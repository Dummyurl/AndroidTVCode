package com.googleandroidtv.logging;

import com.googleandroidtv.protobuf.nano.CodedOutputByteBufferNano;
import com.googleandroidtv.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;

public final class LeanbackProto$RecommendationInsertAction extends ExtendableMessageNano<LeanbackProto$RecommendationInsertAction> {
    public Integer bucketId;
    public Float normalizedPriority;
    public String packageName;
    public Integer position;
    public Float score;
    public String tagName;
    public Integer unnormalizedPriority;

    public LeanbackProto$RecommendationInsertAction() {
        clear();
    }

    public LeanbackProto$RecommendationInsertAction clear() {
        this.position = null;
        this.packageName = null;
        this.tagName = null;
        this.bucketId = null;
        this.unnormalizedPriority = null;
        this.normalizedPriority = null;
        this.score = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.position != null) {
            output.writeUInt32(1, this.position.intValue());
        }
        if (this.packageName != null) {
            output.writeString(2, this.packageName);
        }
        if (this.tagName != null) {
            output.writeString(3, this.tagName);
        }
        if (this.bucketId != null) {
            output.writeUInt32(4, this.bucketId.intValue());
        }
        if (this.unnormalizedPriority != null) {
            output.writeInt32(5, this.unnormalizedPriority.intValue());
        }
        if (this.normalizedPriority != null) {
            output.writeFloat(6, this.normalizedPriority.floatValue());
        }
        if (this.score != null) {
            output.writeFloat(7, this.score.floatValue());
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.position != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(1, this.position.intValue());
        }
        if (this.packageName != null) {
            size += CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
        }
        if (this.tagName != null) {
            size += CodedOutputByteBufferNano.computeStringSize(3, this.tagName);
        }
        if (this.bucketId != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(4, this.bucketId.intValue());
        }
        if (this.unnormalizedPriority != null) {
            size += CodedOutputByteBufferNano.computeInt32Size(5, this.unnormalizedPriority.intValue());
        }
        if (this.normalizedPriority != null) {
            size += CodedOutputByteBufferNano.computeFloatSize(6, this.normalizedPriority.floatValue());
        }
        if (this.score == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeFloatSize(7, this.score.floatValue());
    }
}
