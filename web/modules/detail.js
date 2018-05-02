import React, { Component }  from 'react'
import ReactQuill from 'react-quill'

var Detail= React.createClass({
    displayName: 'Detail',
    propTypes: {
        userData: React.PropTypes.arrayOf(
            React.PropTypes.string
        ),
        initialData: React.PropTypes.arrayOf(
                React.PropTypes.string
        ),
        toolbarOptions: React.PropTypes.arrayOf(
            React.PropTypes.arrayOf(
                React.PropTypes.string
            )
        ),
    },

    getInitialState: function() {
        return {
            detailData:this.props.initialData,
            user:this.props.userData,
            showRef:false,
            text:null,
            record:false,
            recordData:[],
            refText:null,
            answer:null,
        };
    },

    showReference: function(){
        var ref=document.getElementById("reference");
        ref.style.display="block";
        this.setState({showRef:true});
        if (this.state.detailData[2]!==null){
            var reff=document.getElementById("refContent");
            reff.innerHTML=this.state.detailData[2];
        }
    },

    hideReference: function(){
        var ref=document.getElementById("reference");
        ref.style.display="none";
        this.setState({showRef:false});
    },

    ref:"",

    saveRef: function(value){
        this.ref=value;
        this.setState({refText:value});
    },

    saveUpdateRef: function(){

        this.serverRequest9 = $.post("/MnpltServlet",{add:"0",delete:"0",update:"1",
            key:this.state.detailData[5],name:null,content:null,reference:this.ref}, function (data) {
            var pop=document.getElementById("popref");
            pop.style.display="none";
            var temp=this.state.detailData;
            temp[2]=this.ref;
            this.setState({detailData:temp});
            var c=document.getElementById("refContent");
            c.innerHTML=this.ref;
        }.bind(this));
    },

    popRef: function(){
        var pop=document.getElementById("popref");
        pop.style.display="block";
    },

    handleCloseRef:function(){
        var pop=document.getElementById("popref");
        pop.style.display="none";
    },
    //editor

    handleChangeEditor: function(value){
        this.setState({text:value});
    },

    updateFre: function(){
        this.serverRequest11 = $.get("/MnpltServlet?add=0&delete=0&update=1&key=" +this.state.detailData[5]+
            "&name=&content=&reference=&tagOne=&tagTwo=&frequency=1&date=0", function (data) {
        }.bind(this));
    },

    updateDate: function(){
        this.serverRequest12 = $.get("/MnpltServlet?add=0&delete=0&update=1&key=" +this.state.detailData[5]+
            "&name=&content=&reference=&tagOne=&tagTwo=&frequency=0&date=1", function (data) {
        }.bind(this));
    },

    handleSubmitEditor: function(){
        var temp;
        this.serverRequest16 = $.get("/QueryRecordServlet?libraryid="+this.state.detailData[5], function (data) {
            var tem=JSON.parse(data);
            tem.sort(function(a, b) {
                return a[0]<b[0];
            });
            //this.setState({recordData:temp});
            temp=tem;
            var key;


            if (temp===null || temp.length===0){
                key=1;
            }
            else{
                key=parseInt(temp[0][0])+1;
            }

            this.serverRequest13 = $.post("/AddRecordServlet",{libraryid:this.state.detailData[5],
                recordid:key.toString(),answer:this.state.text}, function (data) {
                this.setState({text:null});
            }.bind(this));

        }.bind(this));

        this.updateFre();
        this.updateDate();
    },

    //record

    showRecord: function(){
        this.serverRequest10 = $.get("/QueryRecordServlet?libraryid="+this.state.detailData[5], function (data) {
            var temp=JSON.parse(data);
            temp.sort(function(a, b) {
                return a[0]<b[0];
            });
            this.setState({record:true,recordData:temp});
        }.bind(this));
    },

    hideRecord: function(){
        this.setState({record:false,answer:null});
    },



    showRecordDetail: function(e){
        var id=e.target.id;
        var r=parseInt(id.substring(0,id.indexOf("r")));
        var rec=this.state.recordData[r][0];
        this.serverRequest14 = $.get("/QueryRecordDetailServlet?libraryid="+this.state.detailData[5]
            +"&recordid="+rec, function (data) {
            // this.setState({answer:data});
            var d=document.getElementById("recordDetail");
            d.innerHTML=data;
        }.bind(this));
    },
    renderPopRef: function(){
        return(
            <div id="popref">
                <form>
                    <div>ref: <ReactQuill id="questionref" modules={{ formula: true, toolbar:this.props.toolbarOptions}} style={{height:"200px"}} value={this.state.refText}
                                          onChange={this.saveRef} /></div>
                    <input type="button" value="submit" onClick={this.saveUpdateRef}/>
                </form>
                <button className="close" onClick={this.handleCloseRef}>close</button>
            </div>
        )
    },

    renderRefBtn: function(){
        if (this.state.showRef===true){
            return <button onClick={this.hideReference}>hide reference</button>
        }
        else{
            return <button onClick={this.showReference}>show reference</button>
        }
    },

    renderRef: function(){
        if (this.state.detailData[2]===null){
            if (this.state.detailData[6]===this.state.user[0]) {
                return (
                    <div id="reference">
                        <p id="refContent">no reference yet, create one?</p>
                        <button onClick={this.popRef} id="createRef">Create</button>
                    </div>
                )
            }
            else{
                return(
                    <div id="reference">
                        <p id="refContent">no reference yet</p>
                    </div>
                )
            }
        }
        else{
            if (this.state.detailData[6]===this.state.user[0]) {
                return (
                    <div id="reference">
                        <div id="refContent">{this.state.detailData[2]}</div>
                        <button onClick={this.popRef} id="editRef">Edit</button>
                    </div>
                )
            }
            else{
                return (
                    <div id="reference">
                        <div id="refContent">{this.state.detailData[2]}</div>
                    </div>
                )
            }
        }
    },

    renderEditor: function(){
        return (
            <div id="editor">
                <ReactQuill modules={{ formula: true, toolbar:this.props.toolbarOptions}} style={{height:"200px"}} value={this.state.text}
                            onChange={this.handleChangeEditor} />
                <button id="editorbtn" onClick={this.handleSubmitEditor}>submit</button>
            </div>
        )
    },

    renderDetail: function(){
        if (this.state.record===false){
            return(
                <div>
                    <p id="detailContent"> </p>
                    <button onClick={this.showRecord}>your answer history</button>
                    {this.renderRefBtn()}
                    {this.renderRef()}
                    {this.renderPopRef()}
                    {this.renderEditor()}
                </div>
            )}
        else{
            return(
                <div>
                    <button id="recordbackbtn" onClick={this.hideRecord}>back</button>
                    <div id="recordDetail"> </div>
                    <table>
                        <tbody>
                        <tr>
                            <th>#</th>
                            <th>Date</th>
                        </tr>
                        {this.state.recordData.map(function(row, rowidx) {
                            return (
                                <tr id={"r"+rowidx.toString()} key={"r"+rowidx.toString()}>{
                                    row.map(function(cell, idx) {
                                        return <td onClick={this.showRecordDetail}
                                                   id={rowidx.toString()+"r"+idx.toString()} key={"r"+idx.toString()}>{cell}</td>;
                                    }, this)}
                                </tr>
                            );
                        }, this)}
                        </tbody>
                    </table>

                </div>
            )
        }
    },

    render:function(){
        return(
            <div className="detailPage">
                <h id="detailTitle">{this.state.detailData[0]}</h>
                <p id="detailOwner">contributor: {this.state.detailData[3]}</p>
                <p id="detailDate">updated: {this.state.detailData[4]}</p>
                {this.renderDetail()}
            </div>
        )
    }

})

export  default Detail;