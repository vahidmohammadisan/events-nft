package ir.vahidmohammadisan.lst.data.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.5.0.
 */
@SuppressWarnings("rawtypes")
public class Evn extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5061099c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80630b79143014610067578063330a347b1461009757806335046722146100b35780634895e80a146100e357806385ebbe7314610114578063db616a8d14610144575b600080fd5b610081600480360381019061007c91906105ca565b610160565b60405161008e9190610638565b60405180910390f35b6100b160048036038101906100ac91906105ca565b61019e565b005b6100cd60048036038101906100c8919061067f565b6103d1565b6040516100da91906106bb565b60405180910390f35b6100fd60048036038101906100f891906105ca565b6103f2565b60405161010b9291906106d6565b60405180910390f35b61012e600480360381019061012991906105ca565b610495565b60405161013b919061071a565b60405180910390f35b61015e600480360381019061015991906105ca565b6104b5565b005b60006020528060005260406000206000915090508060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905081565b6001600082815260200190815260200160002060009054906101000a900460ff16156101ff576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101f690610792565b60405180910390fd5b6000806000838152602001908152602001600020600001805490501161025a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610251906107fe565b60405180910390fd5b6000806000838152602001908152602001600020600001805490509050600081428460405160200161028d92919061083f565b6040516020818303038152906040528051906020012060001c6102b0919061089a565b9050600080600085815260200190815260200160002060000182815481106102db576102da6108cb565b5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508060008086815260200190815260200160002060010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600180600086815260200190815260200160002060006101000a81548160ff0219169083151502179055508073ffffffffffffffffffffffffffffffffffffffff16847ff184af570dff3a3eecc3464b3ee06c5b91b8cbd63a658135ded4f57fc364357960405160405180910390a350505050565b60008173ffffffffffffffffffffffffffffffffffffffff16319050919050565b6000806001600084815260200190815260200160002060009054906101000a900460ff16610455576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161044c90610946565b60405180910390fd5b8260008085815260200190815260200160002060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1691509150915091565b60016020528060005260406000206000915054906101000a900460ff1681565b6001600082815260200190815260200160002060009054906101000a900460ff1615610516576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161050d90610792565b60405180910390fd5b600080828152602001908152602001600020600001339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600080fd5b6000819050919050565b6105a781610594565b81146105b257600080fd5b50565b6000813590506105c48161059e565b92915050565b6000602082840312156105e0576105df61058f565b5b60006105ee848285016105b5565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610622826105f7565b9050919050565b61063281610617565b82525050565b600060208201905061064d6000830184610629565b92915050565b61065c81610617565b811461066757600080fd5b50565b60008135905061067981610653565b92915050565b6000602082840312156106955761069461058f565b5b60006106a38482850161066a565b91505092915050565b6106b581610594565b82525050565b60006020820190506106d060008301846106ac565b92915050565b60006040820190506106eb60008301856106ac565b6106f86020830184610629565b9392505050565b60008115159050919050565b610714816106ff565b82525050565b600060208201905061072f600083018461070b565b92915050565b600082825260208201905092915050565b7f4576656e742068617320616c726561647920656e646564000000000000000000600082015250565b600061077c601783610735565b915061078782610746565b602082019050919050565b600060208201905081810360008301526107ab8161076f565b9050919050565b7f4e6f207061727469636970616e747320666f7220746865206576656e74000000600082015250565b60006107e8601d83610735565b91506107f3826107b2565b602082019050919050565b60006020820190508181036000830152610817816107db565b9050919050565b6000819050919050565b61083961083482610594565b61081e565b82525050565b600061084b8285610828565b60208201915061085b8284610828565b6020820191508190509392505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b60006108a582610594565b91506108b083610594565b9250826108c0576108bf61086b565b5b828206905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b7f4576656e7420686173206e6f7420656e64656420796574000000000000000000600082015250565b6000610930601783610735565b915061093b826108fa565b602082019050919050565b6000602082019050818103600083015261095f81610923565b905091905056fea26469706673582212208af2ae169095565e6a8d936b133edbac8f47a91aeef075f072cdab8db0e40a0664736f6c63430008120033\n";

    public static final String FUNC_ENDEVENT = "endEvent";

    public static final String FUNC_ENROLLFOREVENT = "enrollForEvent";

    public static final String FUNC_ENDEDEVENTS = "endedEvents";

    public static final String FUNC_EVENTS = "events";

    public static final String FUNC_GETADDRESSBALANCE = "getAddressBalance";

    public static final String FUNC_GETWINNERFOREVENT = "getWinnerForEvent";

    public static final Event EVENTENDED_EVENT = new Event("EventEnded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected Evn(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Evn(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Evn(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Evn(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> endEvent(BigInteger _eventID) {
        final Function function = new Function(
                FUNC_ENDEVENT, 
                Arrays.<Type>asList(new Uint256(_eventID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enrollForEvent(BigInteger _eventID) {
        final Function function = new Function(
                FUNC_ENROLLFOREVENT, 
                Arrays.<Type>asList(new Uint256(_eventID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static EventEndedEventResponse getEventEndedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(EVENTENDED_EVENT, log);
        EventEndedEventResponse typedResponse = new EventEndedEventResponse();
        typedResponse.log = log;
        typedResponse.eventID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.selectedAddress = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<EventEndedEventResponse> eventEndedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEventEndedEventFromLog(log));
    }

    public Flowable<EventEndedEventResponse> eventEndedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EVENTENDED_EVENT));
        return eventEndedEventFlowable(filter);
    }

    public RemoteFunctionCall<Boolean> endedEvents(BigInteger param0) {
        final Function function = new Function(FUNC_ENDEDEVENTS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> events(BigInteger param0) {
        final Function function = new Function(FUNC_EVENTS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getAddressBalance(String _userAddress) {
        final Function function = new Function(FUNC_GETADDRESSBALANCE, 
                Arrays.<Type>asList(new Address(160, _userAddress)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, String>> getWinnerForEvent(BigInteger _eventID) {
        final Function function = new Function(FUNC_GETWINNERFOREVENT, 
                Arrays.<Type>asList(new Uint256(_eventID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, String>>(function,
                new Callable<Tuple2<BigInteger, String>>() {
                    @Override
                    public Tuple2<BigInteger, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, String>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    @Deprecated
    public static Evn load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Evn(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Evn load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Evn(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Evn load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Evn(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Evn load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Evn(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Evn> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Evn.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Evn> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Evn.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Evn> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Evn.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Evn> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Evn.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class EventEndedEventResponse extends BaseEventResponse {
        public BigInteger eventID;

        public String selectedAddress;
    }
}
