
import React  from 'react'
import CommonSet from './commonTable'
import ReactQuill from 'react-quill'

var Wrong=React.createClass(
    {
       displayName: 'Wrong',
        propTypes:{
            headers: React.PropTypes.arrayOf(
                React.PropTypes.string
            ),
            toolbarOptions: React.PropTypes.arrayOf(
                React.PropTypes.arrayOf(
                    React.PropTypes.string
                )
            ),
        },

        getInitialState:function() {
            return {
                data: null,
                dataCom:null,
                sortby: null,
                descending: false,
                edit: null, // [row index, cell index],
                search: false,
                login:false,
                register:false,
                user:null,
                loginPattern:1,
                registerPattern:1,
                logoutPattern:1,
                tagPattern:0,
                delete:false,
                detail:false,
                detailData:[],
                showRef:false,
                text:null,
                record:false,
                recordData:[],
                answer:null,
                tags:[],
                addTagOne:"",
                addTagTwo:"",
                self:false,
                common:false,
                detailCom:false,
                addText:null,
                refText:null
            };
        },

        componentWillMount:function(){
            this.serverRequest21=$.get("/QueryCommonServlet",function(data){
                var temp=JSON.parse(data);

                this.setState({
                    dataCom:temp,
                    detail:false,
                    self:false,
                    common:true
                });
            }.bind(this));
        },

        queryQuestion:function(){
            this.serverRequest=$.get("/QueryServlet",function(data){

                var temp=JSON.parse(data);
                if (temp[0][0]==="-1"){
                    temp=null;
                }
                this.setState({
                    data:temp,
                    detail:false,
                    self:true,
                    common:false
                });

            }.bind(this));
        },

        queryManQuestion: function(){
            this.setState({
                detail:false,
                self:false,
                common:true
            });

        },

        sortTuple:function(e){
            var column = e.target.cellIndex;
            var data = this.state.data.slice();
            var descending = this.state.sortby === column && !this.state.descending;
            data.sort(function(a, b) {
                return descending
                    ? (a[column] < b[column] ? 1 : -1)
                    : (a[column] > b[column] ? 1 : -1);
            });
            this.setState({
                data: data,
                sortby: column,
                descending: descending,
                text:""
            });
        },

        showEditor: function(e) {
            if (this.state.data[e.target.dataset.row][5]===this.state.user[0]){
                this.setState({edit: {
                        row: parseInt(e.target.dataset.row),
                        cell: e.target.cellIndex,
                    }});
            }
        },

        saveEditor: function(e) {
            e.preventDefault();


            //MnpltServlet?add=1/0&delete=1/0&update=1/0&key=&name=&content=&reference=&tagOne=&tagTwo=&frequency=1/0&date=1/0
            if (this.state.edit.cell===1) {
                var input = e.target.firstChild;
                var data = this.state.data.slice();
                data[this.state.edit.row][this.state.edit.cell] = input.value;
                this.serverRequest5 = $.get("/MnpltServlet?add=0&delete=0&update=1&key=" + data[this.state.edit.row][0]
                    + "&name="+input.value+"&content=&reference=&tagOne=&tagTwo=&frequency=0&date=0", function (data) {

                }.bind(this));
                this.setState({
                    edit: null,
                    data: data,});
            }
            else if (this.state.edit.cell===2){
                var input = e.target.firstChild;
                var data = this.state.data.slice();
                if (this.state.tagPattern===0){
                    var temp=data[this.state.edit.row][this.state.edit.cell];
                    var i=temp.indexOf(" ");
                    var s=temp.substring(i);
                    data[this.state.edit.row][this.state.edit.cell]=input.value+s;

                    this.serverRequest5 = $.get("/MnpltServlet?add=0&delete=0&update=1&key=" + data[this.state.edit.row][0]
                        + "&name=&content=&reference=&tagOne="+input.value, function (data) {
                    }.bind(this));
                    this.setState({
                        edit: null,
                        data: data,
                        tagPattern:1
                    });
                }
                else{
                    var temp=data[this.state.edit.row][this.state.edit.cell];
                    var i=temp.indexOf(" ");
                    var s=temp.substring(0,i+1);
                    data[this.state.edit.row][this.state.edit.cell]=s+input.value;
                    this.serverRequest5 = $.get("/MnpltServlet?add=0&delete=0&update=1&key=" + data[this.state.edit.row][0]
                        + "&name=&content=&reference=&tagOne=&tagTwo="+input.value, function (data) {
                    }.bind(this));
                    this.setState({
                        edit: null,
                        data: data,
                        tagPattern:0
                    });
                }
            }
        },

        preData: null,

        searchPattern:function() {
            if (this.state.search) {
                this.setState({
                    data: this.preData,
                    search: false,
                });
                this.preData = null;
            } else {
                this.preData = this.state.data;
                this.setState({
                    search: true,
                });
            }
        },

        searchFilter: function(e) {
            var keyWord = e.target.value.toLowerCase();
            if (!keyWord) {
                this.setState({data: this.preData});
                return;
            }
            var idx = e.target.dataset.idx;
            var searchdata = this.preData.filter(function(row) {
                return row[idx].toString().toLowerCase().indexOf(keyWord) > -1;
            });
            this.setState({data: searchdata});
        },

        //login

        loginId:null,
        loginPwd:null,

        saveLoginId: function(e){
            this.loginId=e.target.value;
        },

        saveLoginPwd: function(e){
            this.loginPwd=e.target.value;
        },

        saveLogin: function(){
            this.serverRequest2=$.get("/LoginServlet?userid="+this.loginId+"&password="+this.loginPwd
                ,function(data){
                    var user=data;
                    if (user[0]==="-1"){
                        this.setState({
                            loginPattern:3
                        })
                    }
                    else if (user[0]==="0"){
                        this.setState({
                            loginPattern:2
                        })
                    }
                    else{
                        this.setState({
                            user:user,
                            login:true,
                            loginPattern:0
                        });
                    }
                    this.queryQuestion();
                }.bind(this));
        },

        changePatternLogin: function(){
            this.setState({
                loginPattern:1
            });
        },

        handleCloseLogin: function(){
            var pop=document.getElementById("poplogin");
            pop.style.display="none";
            this.setState({
                loginPattern:1
            });
        },

        login: function(){
            var pop=document.getElementById("poplogin");
            pop.style.display="block";
        },

        //register

        registerName:null,
        registerPwd:null,
        registerPwdCp:null,
        registerEmail:null,
        registerPhone:null,
        registerId:null,

        saveRegisterName: function(e){
            this.registerName=e.target.value;
            let name=this.registerName;
            if (name.length > 10){
                var pop=document.getElementById("usernamehint");
                pop.style.display="block";
            }
            else{
                var pop=document.getElementById("usernamehint");
                pop.style.display="none";
            }
        },

        saveRegisterPwd: function(e){
            this.registerPwd=e.target.value;
            let password=this.registerPwd;
            let p1=/[0-9]/;
            let p2=/[a-zA-Z]/i;
            if (password.length >=6 && p1.test(password) && p2.test(password) ){
                var pop=document.getElementById("passwordhint");
                pop.style.display="none";
            }
            else{
                var pop=document.getElementById("passwordhint");
                pop.style.display="block";
            }
        },

        saveRegisterPwdCp: function(e){
            this.registerPwdCp=e.target.value;
            if (this.registerPwd === this.registerPwdCp){
                var pop=document.getElementById("passwordcphint");
                pop.style.display="none";
            }
            else{
                var pop=document.getElementById("passwordcphint");
                pop.style.display="block";
            }
        },

        saveRegisterEmail: function(e){
            this.registerEmail=e.target.value;
            let email=this.registerEmail;
            if (email.indexOf("@") !== -1 && (email.indexOf(".com") !== -1 || email.indexOf(".cn") !== -1)){
                var pop=document.getElementById("useremailhint");
                pop.style.display="none";
            }
            else{
                var pop=document.getElementById("useremailhint");
                pop.style.display="block";
            }
        },

        saveRegisterPhone: function(e){
            this.registerPhone=e.target.value;
            let phone=this.registerPhone;
            let p=/^\+?[1-9][0-9]*$/;
            if (phone.length !== 11 || !p.test(phone)){
                var pop=document.getElementById("userphonehint");
                pop.style.display="block";
            }
            else{
                var pop=document.getElementById("userphonehint");
                pop.style.display="none";
            }
        },

        saveRegister: function(e){
            this.serverRequest3=$.get("/RegisterServlet?username="+this.registerName+"&password="+this.registerPwd
                +"&email="+this.registerEmail+"&phone="+this.registerPhone
                ,function(data){
                    var user=JSON.parse(data);
                    if (user[0]==="0"){
                        this.setState({
                            registerPattern:2
                        })
                    }
                    else{
                        this.registerId=user[0];
                        this.setState({
                            registerPattern:0,
                        });
                    }
                }.bind(this));
        },

        changePatternRegister: function(){
            this.setState({
                registerPattern:1
            });
        },

        handleCloseRegister: function(){
            var pop=document.getElementById("popregister");
            pop.style.display="none";
            this.setState({
                registerPattern:1
            });
        },

        register: function(){
            var pop=document.getElementById("popregister");
            pop.style.display="block";
        },

        //logout

        saveLogout: function(){
            this.serverRequest4=$.get("/LogoutServlet",function(data){
                this.setState({
                    logoutPattern:0,
                    login:false,
                    record:false
                });
            }.bind(this));
        },

        handleCloseLogout: function(){
            var pop=document.getElementById("poplogout");
            pop.style.display="none";
            this.setState({
                logoutPattern:1
            });
        },

        logout: function(){
            var pop=document.getElementById("poplogout");
            pop.style.display="block";
        },

        //usercenter

        handleCloseCenter: function(){
            var pop=document.getElementById("popusercenter");
            pop.style.display="none";
        },

        userCenter: function(){
            var pop=document.getElementById("popusercenter");
            pop.style.display="block";
        },

        //add

        addName:"",
        addContent:"",
        addTagOne:"",
        addTagTwo:"",

        saveAddName: function(e){
            this.addName=e.target.value;
        },

        saveAddContent: function(value){
            this.addContent=value;
            this.setState({addText:value});
        },

        saveAddTagOne: function(e){
            this.addTagOne=e.target.value;
            this.setState({addTagOne:e.target.value});
        },

        saveAddTagTwo: function(e){
            this.addTagTwo=e.target.value;
            this.setState({addTagTwo:e.target.value});
        },
        // MnpltServlet?add=1/0&delete=1/0&update=1/0&key=&name=&content=&reference=&tagOne=&tagTwo=&frequency=1/0&date=1/0
        saveAdd: function(){
            var key=null;
            if(this.state.data.length===0){key=1;}
            else {
                key = parseInt(this.state.data[this.state.data.length - 1][0]) + 1;
            }
            this.serverRequest6=$.post("/MnpltServlet",{add:"1",delete:"0",update:"0",
                key: key.toString(), name:this.addName,content:this.addContent,
                reference:null,tagOne:this.addTagOne,tagTwo:this.addTagTwo},function(data){
                var pop=document.getElementById("popadd");
                pop.style.display="none";
                this.addName="";
                this.addContent="";
                this.addTagOne="";
                this.addTagTwo="";
                this.setState({addTagOne:"",addTagTwo:"",addText:null});
                this.queryQuestion();
            }.bind(this));
        },



        handleCloseAdd:function(){
            var pop=document.getElementById("popadd");
            pop.style.display="none";
            this.addName="";
            this.addContent="";
            this.addTagOne="";
            this.addTagTwo="";
            this.setState({addTagOne:"",addTagTwo:""});
        },

        addQuestion: function(){
            var pop=document.getElementById("popadd");
            pop.style.display="block";
        },

        //delete

        showDelete: function(){
            if (!this.state.delete) {
                var pop = document.getElementsByClassName("delete");
                for (var i=0;i<pop.length;i++){
                    pop[i].style.color = "#a1263b";
                }
            }
            else{
                var pop = document.getElementsByClassName("delete");
                for (var i=0;i<pop.length;i++){pop[i].style.color = "#2589bf";}
            }
            this.setState({delete:!this.state.delete});
        },

        deleteQuestion: function(e){

            var id=e.target.id;
            var key=document.getElementById(id).innerHTML;
            this.serverRequest7=$.get("/MnpltServlet?add=0&delete=1&update=0&key="
                +key,function(data){
                this.queryQuestion();
            }.bind(this));
        },

        //detail

        detailQuestion: function(e){
            var name=e.target.id;
            var k=name.indexOf("r");
            var id=name.substring(0,k)+"0";
            var key=document.getElementById(id).innerHTML;
            this.serverRequest8=$.get("/DetailServlet?key="
                +key,function(data){
                var d=JSON.parse(data);
                this.setState({detail:true,self:false,detailData:d});
                var c=document.getElementById("detailContent");
                c.innerHTML=d[1];
            }.bind(this));
        },


        //reference

        showReference: function(){
            var ref=document.getElementById("reference");
            ref.style.display="block";
            this.setState({showRef:true});
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

        render: function() {
            return (
                <div>
                    <div>
                        {this.renderToolbar()}
                        {this.renderTable()}
                        {this.renderPopLogin()}
                        {this.renderPopRegister()}
                        {this.renderPopLogout()}
                        {this.renderPopUserCenter()}
                        {this.renderPopAdd()}
                    </div>
                </div>
            );
        },

        renderPopLogin: function(){
            if (this.state.loginPattern===1) {
                return (
                    <div id="poplogin">
                        <form>
                            <p>id: <input type="text" name="id" onChange={this.saveLoginId}/></p>
                            <p>password: <input type="password" name="password" onChange={this.saveLoginPwd}/>
                            </p>
                            <input type="button" value="submit" onClick={this.saveLogin}/>
                        </form>
                        <button className="close" onClick={this.handleCloseLogin}>close</button>
                    </div>
                )
            }
            else if (this.state.loginPattern===2){
                return(
                    <div id="poplogin">
                        <p>wrong password</p>
                        <button onClick={this.changePatternLogin}>try again</button>
                        <button className="close" onClick={this.handleCloseLogin}>close</button>
                    </div>
                )
            }
            else if (this.state.loginPattern===0){
                return(
                    <div id="poplogin">
                        <p>hi,{this.state.user[1]}</p>
                        <button className="close" onClick={this.handleCloseLogin}>close</button>
                    </div>
                )
            }
            else{
                return(
                    <div id="poplogin">
                        <p>userid no existence</p>
                        <button onClick={this.changePatternLogin}>try again</button>
                        <button className="close" onClick={this.handleCloseLogin}>close</button>
                    </div>
                )
            }
        },

        renderPopRegister: function(){
            if (this.state.registerPattern===1){
                return(
                    <div id="popregister">
                        <form>
                            <p>username: <input type="text" name="username" onChange={this.saveRegisterName}/></p>
                            <p id="usernamehint">最多10个字</p>
                            <p>password: <input type="password" name="password" onChange={this.saveRegisterPwd}/></p>
                            <p id="passwordhint">字母数字混合，最少6位</p>
                            <p>password: <input type="password" name="password" onChange={this.saveRegisterPwdCp}/></p>
                            <p id="passwordcphint">两次密码不一致</p>
                            <p>email: <input type="text" name="email" onChange={this.saveRegisterEmail}/></p>
                            <p id="useremailhint">邮件格式不正确</p>
                            <p>phone number: <input type="text" name="phone" onChange={this.saveRegisterPhone}/></p>
                            <p id="userphonehint">11位手机号</p>
                            <input type="button" value="submit" onClick={this.saveRegister}/>
                        </form>
                        <button className="close" onClick={this.handleCloseRegister}>close</button>
                    </div>
                )
            }
            else if (this.state.registerPattern===0){
                return(
                    <div id="popregister">
                        <p>success,your id is {this.registerId}.</p>
                        <button className="close" onClick={this.handleCloseRegister}>close</button>
                    </div>
                )
            }
            else {
                return (
                    <div id="popregister">
                        <p>phone has been used</p>
                        <button onClick={this.changePatternRegister}>try again</button>
                        <button className="close" onClick={this.handleCloseRegister}>close</button>
                    </div>
                );
            }
        },

        renderPopLogout: function(){
            if (this.state.logoutPattern===1){
                return(
                    <div id="poplogout">
                        <p>Are you sure to log out?</p>
                        <button onClick={this.saveLogout}>Yes</button>
                        <button onClick={this.handleCloseLogout}>No</button>
                    </div>
                )
            }
            else{
                return (
                    <div id="poplogout">
                        <p>success</p>
                        <button className="close" onClick={this.handleCloseLogout}>close</button>
                    </div>
                )
            }
        },

        renderPopUserCenter: function(){
            if (this.state.user) {
                return (
                    <div id="popusercenter">
                        <p>id: {this.state.user[0]}</p>
                        <p>name: {this.state.user[1]}</p>
                        <p>email: {this.state.user[2]}</p>
                        <p>phone: {this.state.user[3]}</p>
                        <button className="close" onClick={this.handleCloseCenter}>close</button>
                    </div>
                )
            }
        },

        renderPopAdd: function(){
            var ttemp=new Set();
            var list=[];
            var temp=this.state.data;
            if (temp) {
                var tag1, tag2;
                for (var i = 0; i < temp.length; i++) {
                    var str = temp[i][2];
                    var idx = str.indexOf(" ");
                    tag1 = str.substring(0, idx);
                    if (ttemp.has(tag1) === false) {
                        ttemp.add(tag1);
                        list.push(tag1);
                    }
                    if (idx !== length - 1) {

                        tag2 = str.substring(idx + 1);
                        if (ttemp.has(tag2) === false) {
                            ttemp.add(tag2);
                            list.push(tag2);
                        }
                    }
                }
            }
            let tagO=list.map(function(item) {
                if (item.indexOf(this.state.addTagOne) !== -1) return (<div className="tagHint" key={"1"+item}>{item}</div>)
            },this);
            let tagT=list.map(function(item) {
                if (item.indexOf(this.state.addTagTwo) !== -1) return (<div className="tagHint" key={"2"+item}>{item}</div>)
            },this);
            return(
                <div id="popadd">
                    <form>
                        <p>name: <input type="text" name="questionname" onChange={this.saveAddName}/></p>
                        <div >content: <ReactQuill id="questioncontent" modules={{ formula: true, toolbar:this.props.toolbarOptions}} style={{height:"200px"}} value={this.state.addText}
                                                   onChange={this.saveAddContent} /></div>
                        <p>tagone: <input type="text"  id="questiontagone" name="questiontagone" onChange={this.saveAddTagOne}/></p>
                        <div className="tag">{tagO}</div>
                        <p>tagtwo: <input type="text" id="questiontagtwo" name="questiontagtwo" onChange={this.saveAddTagTwo}/></p>
                        <div className="tag">{tagT}</div>
                        <input type="button" value="submit" onClick={this.saveAdd}/>
                    </form>
                    <button className="close" onClick={this.handleCloseAdd}>close</button>
                </div>
            )
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

        renderToolbar: function(){
            return (
                <div>
                    <div className="Toolbar">
                        <button onClick={this.queryManQuestion}>Popular Wrong Set</button>
                        <button onClick={this.queryQuestion}>My Wrong Set</button>
                    </div>
                    {this.renderMnplt()}
                    {this.renderLogin()}
                </div>

            )
        },

        renderMnplt: function(){
            if (this.state.login && this.state.self){
                return (
                    <div className="Mnpltbar" >
                        <button onClick={this.searchPattern}>Search</button>
                        <button onClick={this.addQuestion}>Add</button>
                        <button onClick={this.showDelete}>Delete</button>
                    </div>)
            }
        },

        renderLogin: function(){
            if (this.state.login===false){
                return (
                    <div className="Userbar">
                        <button id="loginbtn" onClick={this.login}>Login</button>
                        <button id="registerbtn" onClick={this.register}>Register</button>
                    </div>
                )
            }
            else{
                return(
                    <div className="Userbar">
                        <button id="logoutbtn" onClick={this.logout}>Logout</button>
                        <button id="usercenterbtn" onClick={this.userCenter}>User Center</button>
                    </div>
                )
            }
        },

        renderSearchTable: function(){
            if (!this.state.search) {
                return null;
            }
            return (
                <tr onChange={this.searchFilter}>
                    {this.props.headers.map(function(_ignore, idx) {
                        return <td key={idx}><input type="text" data-idx={idx}/></td>;
                    })}
                </tr>
            );
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

        renderTable: function(){
            if (this.state.login) {
                if (this.state.self===true && this.state.data){
                    return (
                        <table>

                            <tbody onDoubleClick={this.showEditor}>
                            {this.renderSearchTable()}
                            <tr onClick={this.sortTuple}>
                                {this.props.headers.map(function (title, idx) {
                                    if (this.state.sortby === idx) {
                                        title += this.state.descending ? ' \u2191' : ' \u2193';
                                    }
                                    return (<th key={idx}>{title}</th>);
                                }, this)}
                            </tr>
                            {this.state.data.map(function (row, rowidx) {
                                return (
                                    <tr key={rowidx}>
                                        {row.map(function (cell, idx) {
                                            var content = cell;
                                            var edit = this.state.edit;
                                            //只允许修改name和tags
                                            if (edit && edit.row === rowidx && edit.cell === idx &&(idx===1||idx===2)) {
                                                if (idx===2){
                                                    var i=cell.indexOf(" ");
                                                    var tag1=cell.substring(0,i);
                                                    var tag2=cell.substring(i+1);
                                                    if (this.state.tagPattern===0) {
                                                        content = (
                                                            <p>
                                                                <form onSubmit={this.saveEditor}>
                                                                    <input type="text" defaultValue={tag1}/>
                                                                </form>
                                                                {tag2}
                                                            </p>
                                                        )
                                                    }
                                                    else{
                                                        content = (
                                                            <p>{tag1}
                                                                <form onSubmit={this.saveEditor}>
                                                                    <input type="text" defaultValue={tag2}/>
                                                                </form>
                                                            </p>
                                                        )
                                                    }
                                                }
                                                else {
                                                    content = (
                                                        <form onSubmit={this.saveEditor}>
                                                            <input type="text" defaultValue={cell}/>
                                                        </form>
                                                    )
                                                }
                                            }
                                            if (idx===1){
                                                return <td className="detail"
                                                           id={"td" + rowidx.toString() + "r"+idx.toString()}
                                                           onClick={this.detailQuestion} key={idx}
                                                           data-row={rowidx}>{content}</td>
                                            }
                                            if (idx===0){
                                                if (this.state.delete===true) {
                                                    return <td className="delete"
                                                               id={"td" + rowidx.toString() + idx.toString()}
                                                               onClick={this.deleteQuestion} key={idx}
                                                               data-row={rowidx}>{content}</td>
                                                }
                                                else{
                                                    return <td className="delete"
                                                               id={"td" + rowidx.toString() + idx.toString()}
                                                               key={idx}
                                                               data-row={rowidx}>{content}</td>
                                                }
                                            }
                                            if (idx===5) return null;
                                            else {return <td key={idx} data-row={rowidx}>{content}</td>}
                                        }, this)}

                                    </tr>
                                );
                            }, this)}
                            </tbody>
                        </table>

                    );
                }
                else if (this.state.detail===true){
                    if (this.state.detailData){
                        return(
                            <div className="detailPage">
                                <h id="detailTitle">{this.state.detailData[0]}</h>
                                <p id="detailOwner">contributor: {this.state.detailData[3]}</p>
                                <p id="detailDate">updated: {this.state.detailData[4]}</p>
                                {this.renderDetail()}
                            </div>
                        )
                    }
                }
                else if (this.state.common){
                    return (<CommonSet headers={this.props.headers} initialData={this.state.dataCom}/>)
                }
            }
            else{
                if (this.state.self)
                {return (<p id="loginHint">please log in</p>);}
                else if (this.state.common){
                    return (<CommonSet headers={this.props.headers} initialData={this.state.dataCom}/>)
                }
            }
        },


    }


);




export default Wrong;
