Option Explicit
'***********************************************************
' ����  : �댇�i���o�͌���c�[���@���[�����M�X�N���v�g
' �T�v  : �\�񏈗��Ɏ��s�����Ƃ��ɋN������A�֌W�҂Ƀ��[���Œʒm����
' �쐬��: 2016/02/24  �쐬��: DA
'***********************************************************

'*******************************************************************************
'                             �萔��`
'*******************************************************************************
' �e�֐��߂�l��`
Const iRetOK = 0                        '����
Const iRetNG = -1                       '�ُ�

' ���[���A�h���X��`�i���M�敡���̏ꍇ�A�J���}��؂�j
' Const mailTo = "eiichi_yamaguchi_za@mail.toyota.co.jp"
' Const mailFrom = "eiichi_yamaguchi_za@mail.toyota.co.jp"
Const mailTo = "masato_nishiguchi_za@mail.toyota.co.jp"
Const mailFrom = "masato_nishiguchi_za@mail.toyota.co.jp"
Const title = "�y�e�X�g�z�댇�i���o�͌���c�[�� - �G���[�ʒm"
Const message = "�\�񏈗��ɂăG���[���������܂����B"

Dim body

'*******************************************************************************
'                             �����J�n
'*******************************************************************************
body = message & vbCrLf & vbCrLf & "�}�X�^�I�� = " & Wscript.Arguments.Item(0) & " �Ԏ� = " & Wscript.Arguments.Item(1) & " �\����� = " & Wscript.Arguments.Item(2)
call cmn_sendMessage(mailFrom, mailTo, title, body)

'***********************************************************
' �֐����Fcmn_sendMessage
' �T�v  �F�����̓��e�����[������
' ����  �F1) ���[�����M��
'       �F2) ���[����M��
'       �F3) ���[���^�C�g��
'       �F4) ���[�����b�Z�[�W
' �߂�l�F�Ȃ�
' �쐬���F2016/02/24  �쐬��  �FDA
' �X�V���F�@�@�@�@�@�@�X�V��  �F
'�@�@�@�@�@�@�@�@�@�@ �X�V���e�F
'***********************************************************
Sub cmn_sendMessage(FromMlAddr, ToMlAddr, Title, Message)
    Dim oMsg
    Set oMsg = CreateObject("CDO.Message")
    oMsg.From = FromMlAddr
    oMsg.To = ToMlAddr
    oMsg.Subject = Title
    oMsg.TextBody = Message
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusing") = 2
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpconnectiontimeout") = 30
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserver") = "mail07.toyota.co.jp"
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserverport") = 25
    oMsg.Configuration.Fields.Update
    oMsg.Send
End Sub
